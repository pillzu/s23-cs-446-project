from flask import Flask, request, jsonify
from internal.db import DatabaseConnection
from decouple import config
import logging
import internal.helpers as hp
import uuid
import json
import stripe
from email_validator import validate_email, EmailNotValidError
from internal.helpers import is_valid_phone_number, return_message_response
import spotipy
from spotipy.oauth2 import SpotifyOAuth


app = Flask(__name__)

SPOTIFY_CLIENT_ID = "0ffac1d3b8c545ada41939e91ee75d30"
SPOTIFY_CLIENT_SECRET = "643a8dcc608d4d00af2bd6387e3853df"
SPOTIFY_USER_ID = "31wjtvhoqm75rg3qlyhzqmtslcce"

'''
Parties Endpoints
'''


@app.post("/parties/host")
def host_party():
    """Endpoint to host a party"""
    # add a new party
    req = request.json

    party_id = db.exec_host_party(req["name"], req["party_avatar_url"],
                                  req["date_time"], req["host_id"],
                                  req["max_cap"], req["desc"],
                                  req["entry_fee"], req["street"],
                                  req["city"], req["prov"],
                                  req["postal_code"], req["tags"],
                                  req["type"], req["drug"],
                                  req["byob"], req["host_name"], req["qr_endpoint"])

    if not party_id:
        return jsonify({"message": "Unable to create party. Please try again..."}), 400

    party_auth_manager = SpotifyOAuth(client_id=SPOTIFY_CLIENT_ID,
                                      client_secret=SPOTIFY_CLIENT_SECRET,
                                      redirect_uri="http://localhost:8080",
                                      scope="playlist-modify-private")

    sp = spotipy.Spotify(auth_manager=party_auth_manager)

    access_token = party_auth_manager.get_access_token(as_dict=False)

    # Create a public Spotify playlist for the party and add its ID to the DB
    playlist_name = f"{party_id}"
    playlist = sp.user_playlist_create(SPOTIFY_USER_ID, playlist_name, public=False, collaborative=True)
    playlist_id = playlist["id"]
    db.set_playlist_id(party_id, playlist_id, access_token)
    
    return jsonify({
        "message": "Party successfully hosted"
    }), 200


@app.get("/parties/playlist/<party_id>")
def get_playlist_id(party_id):

    if not party_id:
        return jsonify({
            "message": "Error retrieving playlist info"
        }), 400

    try:
        playlist_id = db.query_spotify_IDs(party_id)[0][1]
    except:
        return jsonify({
            "message": "Error fetching playlist id"
        }), 400

    return jsonify({
        "message": playlist_id
    })

@app.post("/parties")
def get_tagged_parties():
    """Endpoint to get parties with a specific tag"""
    req = request.json
    tags = req.get("tags", None)

    # TODO: Get hostname as well (don't need ID)
    parties = db.query_party(show_detail=True, show_attend_count=True)
    if tags is not None and len(tags) != 0:
        parties = db.query_party(tag_subset=tags, show_detail=True, show_attend_count=True)

    if parties is None:
        return jsonify({"message": "Unable to fetch tagged parties. Please try again..."}), 500

    parties = map(hp.row_to_party, parties)
    return jsonify(list(parties)), 200


@ app.get("/parties/<party_id>")
def get_party_details(party_id):
    party = db.query_party(party_id=party_id, show_detail=True, show_attend_count=True)
    if party is None:
        return {"message": "Party does not exist! Please try again..."}, 404

    # location = db.query_locations(party_id=party_id)
    # if location is None:
    #     return {"message": "Error retrieving party location! Please try again..."}, 500

    party = hp.row_to_party(party[0])
    # location = hp.row_to_location(location[0])

    # resp = {**party, **location}
    resp = {**party}

    return jsonify(resp), 200


@app.post("/parties/attend/<party_id>")
def attend_party(party_id):
    """Endpoint to register attendee to a party"""
    req = request.json
    user = req["user"]
    user_id = user["user_id"]
#     user_id = req.get("user_id", None)
    
    # TODO: Edit frontend connecting to attend_party to ask attendee for a list of Spotify track names
#     track_names = req.get("track_names")
    track_names = req["songList"]

    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400

    # add guest to hosties party
    if not db.exec_attend_party(user_id, party_id):
        return {"message": "Unable to add user as an attendee! Please call help..."}, 500

    access_token = db.query_spotify_IDs(party_id)[0][2]

    sp = spotipy.Spotify(auth=access_token)

    playlist_id = db.query_spotify_IDs(party_id)[0][1]
    
    # Convert track names to track uris for Spotify API parsing
    track_uris = []
    for track_name in track_names:
        result = sp.search(q=track_name, type="track", limit=1)

        if result["tracks"]["items"]:
            track_uri = result["tracks"]["items"][0]["uri"]
            track_uris.append(track_uri)
        else:
            print(f"Track '{track_name}' not found on Spotify.")

    # Add tracks to the playlist
    sp.playlist_add_items(playlist_id, track_uris)
            
    return {"message": "User registered successfully!"}, 200


@app.get("/party/qr/<party_id>/<guest_id>")
def check_party_attendee(party_id, guest_id):
    """Endpoint to confirm attendee to a party"""
    isAttendee = db.check_attends(party_id, guest_id)
    if isAttendee:
        return {"message": "Successfully verified user is an attendee"}, 200
    return {"message": "Failed to verify user! Please contact help"}, 401


'''
User Endpoints
'''


@app.post("/user/parties/attend")
def get_user_attendee_parties():
    """Endpoint to retrieve user's attendee parties"""
    req = request.json
    user_id = req.get("user_id", None)
    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400

    parties = db.show_attended_parties(user_id, show_detail=True, show_attend_count=True)

    if parties is None:
        return {"message": "Failed to get user attended parties! Please contact help"}, 500

    resp = []
    for party in parties:
        parsed_party = hp.row_to_party(party)

        host = db.query_user(parsed_party['host_id'])

        host = hp.row_to_user(host[0])
        qr_endpoint = f"/party/qr/{parsed_party['party_id']}/{parsed_party['host_id']}"

        resp.append({**parsed_party, "qr_endpoint": qr_endpoint,
                     "host_name": f"{host['first_name']} {host['last_name']}"})

    return jsonify(resp), 200


@ app.post("/user/parties/host")
def get_user_host_parties():
    """Endpoint to retrieve user's hosted parties"""
    req = request.json
    user_id = req.get("user_id", None)
    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400

    parties = db.show_hosted_parties(user_id, show_detail=True, show_attend_count=True)

    if parties is None:
        return {"message": "Failed to get user hosted parties! Please contact help"}, 500

    resp = []
    for party in parties:
        parsed_party = hp.row_to_party(party)
        host = db.query_user(parsed_party['host_id'])

        host = hp.row_to_user(host[0])

        resp.append(
            {**parsed_party, "host_name": f" {host['first_name']} {host['last_name']}"})

    return jsonify(resp), 200

# Register if user does not exist or login
@app.post("/user")
def register_or_login_user():
    req = request.json

    # Email validation
    try:
        emailinfo = validate_email(req["email"], check_deliverability=False)
    except EmailNotValidError:
        return return_message_response("Email does not have valid format.", 400)

    # Phone number validation
    # if not is_valid_phone_number(req["phone_no"]):
    #     return return_message_response("Phone number does not have valid format.", 400)

    result = db.add_new_user(req["profile_url"], req["first_name"], req["last_name"], req["phone_no"], req["address_street"],
                req["address_city"], req["address_prov"], req["address_postal"], req["email"], req["user_id"], exec_stmt=True)

    if result is None:
        return return_message_response("Login failure.", 500)
    else:
        return return_message_response("User with user_id {} successfully logged in".format(result), 201)


# ... (existing imports remain unchanged)

@app.route('/user/<user_id>', methods=['PUT'])
def update_user_details(user_id):
    req = request.json

    # Check if the user_id is provided
    if not user_id:
        return return_message_response("Failed: Please provide user_id.", 400)

    # Email validation
    email = req.get("email", None)
    if email:
        try:
            emailinfo = validate_email(email, check_deliverability=False)
        except EmailNotValidError:
            return return_message_response("Email does not have a valid format.", 400)

    # Phone number validation
    phone_no = req.get("phone_no", None)
    if phone_no and not is_valid_phone_number(phone_no):
        return return_message_response("Phone number does not have a valid format.", 400)

    # Extract the user details from the request
    first_name = req.get("first_name", None)
    last_name = req.get("last_name", None)
    address_street = req.get("address_street", None)
    address_city = req.get("address_city", None)
    address_prov = req.get("address_prov", None)
    address_postal = req.get("address_postal", None)

    # Update the user details in the database
    try:
        result = db.update_user(
            user_id,
            first_name=first_name,
            last_name=last_name,
            phone_no=phone_no,
            address_street=address_street,
            address_city=address_city,
            address_prov=address_prov,
            address_postal=address_postal,
            email=email
        )
    except:
        return return_message_response("Internal Server Error", 500)

    if not result:
        return return_message_response("Could not update user details.", 500)
    else:
        return return_message_response("Successfully updated user details.", 200)




@app.route('/user/<user_id>', methods=['DELETE'])
def delete_user_account(user_id):
    if user_id is None:
         return return_message_response("Failed: Please provide user_id.", 500)

    try:
        result = db.delete_user(user_id)
    except:
        return return_message_response("Internal Server Error", 500)

    if not result:
        return return_message_response("User deletion failed. Please check user exists.", 500)
    else:
        return return_message_response("User with user_id {} successfully deleted".format(result), 201)

'''
Payment Endpoint
'''

@app.route('/payment-sheet', methods=['POST'])
def payment_sheet():
    req = request.json

    stripe.api_key = 'sk_test_51NWutYB9GlqxPfMYYnnZDsUZDHf4t8RQvbuzF6RldklWW8KKGBRFxrbAaIDBxkhAeB9D6t9dSn7Ro9kOfHcaA7Ou00tg99GvG3'
    customer = stripe.Customer.create()
    ephemeralKey = stripe.EphemeralKey.create(
        customer=customer['id'],
        stripe_version='2022-11-15',
    )
    paymentIntent = stripe.PaymentIntent.create(
        amount=req["amount"],
        currency='cad',
        customer=customer['id'],
        payment_method_types=['card'],
    )
    return jsonify(paymentIntent=paymentIntent.client_secret,
                    ephemeralKey=ephemeralKey.secret,
                    customer=customer.id,
                    publishableKey='pk_test_51NWutYB9GlqxPfMYGmeB1XiOm376R1cASdzAO5oB37UiDZ9NEb7wJLyx8qN0A2KyhbaxI2LiIqjqhmHHGooY743t00JnjAvV1W')



@app.route('/parties/unattend/<party_id>/<user_id>', methods=['DELETE'])
def unattend_party(party_id, user_id):
    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400
    if party_id is None:
        return {"message": "No party id provided! Please try again..."}, 400

    # remove the guest from the party
    if not db.leave_party(user_id, party_id):
        return {"message": "Unable to unattend the party! Please call help..."}, 500

    return {"message": "Unattended party successfully!"}, 200


@app.route('/parties/cancel/<party_id>', methods=['DELETE'])
def cancel_party(party_id):
    if party_id is None:
        return {"message": "No party id provided! Please try again..."}, 400

    # cancel the party and remove all attendees
    if not db.cancel_party(party_id):
        return {"message": "Unable to cancel the party! Please call help..."}, 500

    return {"message": "Party cancelled successfully!"}, 200



@app.route('/party/update/<party_id>', methods=['PUT'])
def update_party_details(party_id):
    req = request.json

    # Check if the user_id is provided
    if not party_id:
        return {"message": "No party id provided! Please try again..."}, 400

    # Extract the party details from the request
    party_avatar_url = req.get("party_avatar_url", None)
    name = req.get("name", None)
    date_time = req.get("date_time", None)
    host_id = req.get("host_id", None)
    max_cap = req.get("max_cap", None)
    desc = req.get("desc", None)
    entry_fee = req.get("entry_fee", None)
    type = req.get("type", None)
    drug = req.get("drug", None)
    byob = req.get("byob", None)
    host_name = req.get("host_name", None)
    qr_endpoint = req.get("qr_endpoint", None)
    street = req.get("street", None)
    city = req.get("city", None)
    prov = req.get("prov", None)
    postal_code = req.get("postal_code", None)
    tags = req.get("tags", None)

    # Update the party details in the database
    try:
        result = db.update_party(
            party_id,
            party_avatar_url,
            name,
            date_time,
            host_id,
            max_cap,
            desc,
            entry_fee,
            type,
            drug,
            byob,
            host_name,
            qr_endpoint,
            street,
            city,
            prov,
            postal_code,
            tags
        )
    except:
        return return_message_response("Internal Server Error", 500)

    if not result:
        return return_message_response("Could not update party details.", 500)
    else:
        return return_message_response("Successfully updated party details.", 200)



if __name__ == "__main__":
    DB_URL = config("CDB_URL")

    global db
    db = DatabaseConnection(DB_URL)

    if db is None:
        logging.fatal("Unable to initialize Database! Please try again...")
        exit(1)

    db.create_tables()
    app.run(host='0.0.0.0', port=8080)
