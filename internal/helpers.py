from flask import jsonify

def row_to_party(party):
    return {
        "party_id": party[0],
        "name": party[1],
        "date_time": party[2],
        "created_at": party[3],
        "max_cap": party[4],
        "desc": party[5],
        "entry_fee": party[6],
    }


def row_to_user(user):
    return {
        "user_id": user[0],
        "username": user[1],
        "password": user[2],
        "first_name": user[3],
        "last_name": user[4],
        "phone_no": user[5],
        "street": user[6],
        "city": user[7],
        "prov": user[8],
        "postal_code": user[9],
        "email": user[10],
        "party_points": user[11],
    }


def row_to_location(location):
    return {
        "party_id": location[0],
        "street": location[1],
        "city": location[2],
        "prov": location[3],
        "postal_code": location[4]
    }

def is_valid_phone_number(variable):
    if isinstance(variable, (int, float)):
        if len(str(int(variable))) == 10:
            return True
    return False

def return_message_response(message, status):
    response = {
        "message": message
    }
    return jsonify(response), status