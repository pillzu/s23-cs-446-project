from flask import Flask, request, jsonify
from internal.db import DatabaseConnection
from decouple import config
import logging
import internal.helpers as hp
import uuid
import json
from email_validator import validate_email, EmailNotValidError
from internal.helpers import is_valid_phone_number, return_message_response
import spotipy
from spotipy.oauth2 import SpotifyOAuth


app = Flask(__name__)

SPOTIFY_CLIENT_ID = "Preeti should probably check her Discord DMs"
SPOTIFY_CLIENT_SECRET = "Preeti should probably check her Discord DMs"
SPOTIFY_USER_ID = "Preeti should probably check her Discord DMs"

'''
Parties Endpoints
'''


@app.post("/parties/host")
def host_party():
    """Endpoint to host a party"""
    # add a new party
    req = request.json

    party_id = db.exec_host_party(req["name"], req["party_avatar_url"],
                                  req["date_time"], req["user_id"],
                                  req["max_cap"], req["desc"],
                                  req["entry_fee"], req["street"],
                                  req["city"], req["prov"],
                                  req["postal_code"], req["tags"])

    if not party_id:
        return jsonify({"message": "Unable to create party. Please try again..."}), 400

    sp = spotipy.Spotify(auth_manager=SpotifyOAuth(client_id=SPOTIFY_CLIENT_ID,
                                                     client_secret=SPOTIFY_CLIENT_SECRET,
                                                     redirect_uri="http://localhost:8080",
                                                     scope="playlist-modify-public"))

    # Create a public Spotify playlist for the party and add its ID to the DB
    playlist_name = f"{party_id}"
    playlist = sp.user_playlist_create(SPOTIFY_USER_ID, playlist_name, public=True, collaborative=True)
    playlist_id = playlist["id"]
    db.set_playlist_id(party_id, playlist_id)
    
    return jsonify({
        "message": "Party successfully hosted"
    }), 200


@app.post("/parties")
def get_tagged_parties():
    """Endpoint to get parties with a specific tag"""
    req = request.json
    tags = req.get("tags", None)

    # TODO: Get hostname as well (don't need ID)
    parties = db.query_party(show_detail=True)
    if tags is not None and len(tags) != 0:
        parties = db.query_party(tag_subset=tags, show_detail=True)

    if parties is None:
        return jsonify({"message": "Unable to fetch tagged parties. Please try again..."}), 500

    parties = map(hp.row_to_party, parties)
    return jsonify(list(parties)), 200


# TODO: Don't know if we need this right now
# @ app.get("/parties/<party_id>")
# def get_party_details(party_id):
#     party = db.query_party(party_id=party_id)
#     if party is None:
#         return {"message": "Party does not exist! Please try again..."}, 404
#
#     location = db.query_locations(party_id=party_id)
#     if location is None:
#         return {"message": "Error retrieving party location! Please try again..."}, 500
#
#     party = hp.row_to_party(party[0])
#     location = hp.row_to_location(location[0])
#
#     resp = {**party, **location}
#
#     return jsonify(resp), 200
#

@app.post("/parties/attend/<party_id>")
def attend_party(party_id):
    """Endpoint to register attendee to a party"""
    req = request.json
    user_id = req.get("user_id", None)
    
    # TODO: Edit frontend connecting to attend_party to ask attendee for a list of Spotify track names
    track_names = req.get("track_names")

    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400

    # add guest to hosties party
    if not db.exec_attend_party(user_id, party_id):
        return {"message": "Unable to add user as an attendee! Please call help..."}, 500

    sp = spotipy.Spotify(auth_manager=SpotifyOAuth(client_id=SPOTIFY_CLIENT_ID,
                                                     client_secret=SPOTIFY_CLIENT_SECRET,
                                                     redirect_uri="http://localhost:8080",
                                                     scope="playlist-modify-public"))
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

    parties = db.show_attended_parties(user_id, show_detail=True)

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
    parties = db.show_hosted_parties(req["user_id"], show_detail=True)

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


@app.route('/user', methods=['PUT'])
def update_user_details():
    req = request.json

    # Email validation
    try:
        if req.get("email"):
            emailinfo = validate_email(req.get("email"), check_deliverability=False)
    except EmailNotValidError:
        return return_message_response("Email does not have valid format.", 400)

    # Phone number validation
    if req.get("phone_no") and not is_valid_phone_number(req.get("phone_no")):
        return return_message_response("Phone number does not have valid format.", 400)

    try:
        result = db.update_user(req.get("user_id"), req.get("profile_url"), req.get("first_name"), req.get("last_name"), req.get("phone_no"), req.get("address_street"),
                req.get("address_city"), req.get("address_prov"), req.get("address_postal"), req.get("email"))
    except:
        return return_message_response("Internal Server Error", 500)

    if not result:
        return return_message_response("Could not update user details.", 500)
    else:
        return return_message_response("Successfully updated user details.", 500)



@app.route('/user', methods=['DELETE'])
def delete_user_account():
    user_id = request.args.get('user_id')
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



if __name__ == "__main__":
    DB_URL = config("CDB_URL")

    global db
    db = DatabaseConnection(DB_URL)

    if db is None:
        logging.fatal("Unable to initialize Database! Please try again...")
        exit(1)

    db.create_tables()
    app.run(host='0.0.0.0')
