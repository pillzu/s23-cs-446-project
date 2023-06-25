from flask import Flask, request, jsonify
from internal.db import DatabaseConnection
from decouple import config

app = Flask(__name__)


'''
Parties Endpoints
'''


@app.route("/parties/host")
def host_party():
    # add a new party
    req = request.json

    # create a party
    party_id = db.add_new_party(req.party_name, req.date_time,
                                req.max_cap, req.desc,
                                None, None, req.entry_fee)

    if party_id is None:
        return jsonify({"message": "Unable to create party. Please try again..."}), 400

    if not db.set_location(party_id, req.street, req.city,
                           req.prov, req.postal_code):
        return jsonify({"message": "Unable to create party. Please try again..."}), 500

    # add a host
    isHost = db.host_party(req.user_id, party_id)

    if not isHost:
        return jsonify({
            "message": f"Unable to register {req.user_id} as host. Please try again..."
        }), 400

    return jsonify({
        "message": "Party successfully hosted"
    }), 400


@app.post("/parties")
def get_tagged_parties():
    # get party id
    req = request.json

    # request for parties ids using tag
    parties = db.query_tags()
    party_details = []

    for party_id in parties:
        party = db.query_party(party_id=party_id)
        location = db.query_locations(party_id=party_id)
        party_details.append({"party": party, "address": location})

    return party_details, 200


@app.get("/parties/<party_id>")
def get_party_details(party_id):
    party = db.query_party(party_id=party_id)
    if party is None:
        return {"message": "Party does not exist! Please try again..."}, 404

    location = db.query_locations(party_id=party_id)
    if location is None:
        return {"message": "Error retrieving party location! Please try again..."}, 500

    return {"party": party, "address": location}, 200


@app.route("/parties/<party_id>/attend")
def attend_party(party_id):
    # create transaction
    # add guest
    #
    pass


'''
User Endpoints
'''


@ app.post("/user/parties/attend")
def get_user_attend_parties():
    req = request.json
    parties = db.show_attended_parties(req.user_id)
    return parties


@ app.route("/user/parties/host")
def get_user_host_parties():
    req = request.json
    parties = db.show_hosted_parties(req.user_id)
    return parties


if __name__ == "__main__":
    DB_URL = config("CDB_URL")

    global db
    db = DatabaseConnection(DB_URL)

    if db is None:
        log.fatal("Did not work")
        exit(1)
    db.create_tables()

    app.run()
