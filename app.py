from flask import Flask, request, jsonify
from internal.db import DatabaseConnection
from decouple import config
import logging
import internal.helpers as hp
import uuid

app = Flask(__name__)


'''
Parties Endpoints
'''


@app.post("/parties/host")
def host_party():
    """Endpoint to host a party"""
    # add a new party
    req = request.json

    print(req)

    # host_date = req["date_time"]
    # datetime_obj = datetime.strptime(host_date, "%a %b %d %H:%M:%S %Z %Y")

    # create a party
    party_id = db.add_new_party(req["name"], req["date_time"],
                                req["max_cap"], req["desc"],
                                req["entry_fee"])

    if party_id is None:
        return jsonify({"message": "Unable to create party. Please try again..."}), 400

    if not db.set_location(party_id, req["street"], req["city"],
                           req["prov"], req["postal_code"]):
        return jsonify({"message": "Unable to create party. Please try again..."}), 500

    # add a host
    isHost = db.host_party(req["user_id"], party_id)

    if not db.set_tags(party_id, ["EDM"]):
        return jsonify({
            "message": "Unable to set tags. Please try again..."
        }), 400

    if not isHost:
        return jsonify({
            "message": f"Unable to register {req.user_id} as host. Please try again..."
        }), 400

    return jsonify({
        "message": "Party successfully hosted"
    }), 200


@app.post("/parties")
def get_tagged_parties():
    """Endpoint to get parties with a specific tag"""

    # TODO: Use tags
    # get party id
    # req = request.json

    # request for parties ids using tag
    parties = db.query_tags()
    party_details = []

    for party_id, tags in parties:
        party = db.query_party(party_id=party_id)
        location = db.query_locations(party_id=party_id)
        host = db.show_host(party_id, True)

        if not len(parties) or not len(location) or not len(host):
            logging.warn(f"Failed to fetch party with {party_id}...")
            continue

        host = hp.row_to_user(host[0])
        party = hp.row_to_party(party[0])
        location = hp.row_to_location(location[0])

        resp = {**party, **location,
                "host_name": f" {host['first_name']} {host['last_name']}"}

        party_details.append(resp)

    return jsonify(party_details), 200


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

    if user_id is None:
        return {"message": "No user id provided! Please try again..."}, 400

    # get host details
    host = db.show_host(party_id)
    if host is None:
        return {"message": "Failed to verify host of party! Please contact help"}, 500

    host = host[0][0]

    # create transaction
    transaction = db.add_new_transaction(user_id, host, party_id, 0)
    if not transaction:
        return {"message": "Unable to add transaction! Please try again..."}, 500

    # add guest
    if not db.attend_party(user_id, party_id):
        return {"message": "Unable to add user as an attendee! Please call help..."}, 500

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

    parties = db.show_attended_parties(user_id)

    resp = []
    for party_id in parties:
        party_id = party_id[0]
        party = db.query_party(party_id=party_id)
        location = db.query_locations(party_id=party_id)
        host = db.show_host(party_id, True)

        if not len(parties) or not len(location) or not len(host):
            logging.warn(f"Failed to fetch party with {party_id}...")
            continue

        host = hp.row_to_user(host[0])
        party = hp.row_to_party(party[0])
        location = hp.row_to_location(location[0])

        qr_endpoint = f"/party/qr/{party_id}/{user_id}"

        resp.append({**party, **location, "qr_endpoint": qr_endpoint,
                     "host_name": f" {host['first_name']} {host['last_name']}"})

    return jsonify(resp), 200


@ app.post("/user/parties/host")
def get_user_host_parties():
    """Endpoint to retrieve user's hosted parties"""
    req = request.json
    parties = db.show_hosted_parties(req["user_id"])

    resp = []
    for party_id in parties:
        party_id = party_id[0]
        party = db.query_party(party_id=party_id)
        location = db.query_locations(party_id=party_id)
        host = db.show_host(party_id, True)

        if not len(parties) or not len(location) or not len(host):
            logging.warn(f"Failed to fetch party with {party_id}...")
            continue

        host = hp.row_to_user(host[0])
        party = hp.row_to_party(party[0])
        location = hp.row_to_location(location[0])

        resp.append({**party, **location,
                     "host_name": f" {host['first_name']} {host['last_name']}"})

    return jsonify(resp), 200

@app.post("/user")
def register_or_login_user():
    req = request.json
    
    result = add_new_user(self, req["profile_url"], req["first_name"], req["last_name"], req["phone_no"], req["address_street"],
                req["address_city"], req["address_prov"], req["address_postal"], req["email"], uuid.UUID(req["user_id"]), exec_stmt=True)

    if result is None:
        response = {
            message: "Login failure".format(result)
        }
        return jsonify(response), 500
    else:
        response = {
            message: "User with user_id {} successfully logged in".format(result)
        }
        return jsonify(response), 201

@app.route('/user', methods=['PUT'])
def update_user_details():
    # do something

@app.route('/user', methods=['DELETE'])
def delete_user_account():
    # do something


if __name__ == "__main__":
    DB_URL = config("CDB_URL")

    global db
    db = DatabaseConnection(DB_URL)

    if db is None:
        logging.fatal("Unable to initialize Database! Please try again...")
        exit(1)

    db.create_tables()
    app.run(host='0.0.0.0')
