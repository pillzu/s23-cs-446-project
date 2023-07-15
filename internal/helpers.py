
def row_to_party(party):
    return {
        "party_id": party[0],
        "party_avatar_url": party[1],
        "name": party[2],
        "date_time": party[3],
        "host_id": party[4],
        "created_at": party[5],
        "max_cap": party[6],
        "desc": party[7],
        "entry_fee": party[8],
        "street": party[9],
        "city": party[10],
        "prov": party[11],
        "postal_code": party[12],
        "tags": party[13],
    }


def row_to_user(user):
    return {
        "user_id": user[0],
        "profile_url": user[1],
        "first_name": user[2],
        "last_name": user[3],
        "phone_no": user[4],
        "street": user[5],
        "city": user[6],
        "prov": user[7],
        "postal_code": user[8],
        "email": user[9],
        "party_points": user[10],
    }


def row_to_location(location):
    return {
        "party_id": location[0],
        "street": location[1],
        "city": location[2],
        "prov": location[3],
        "postal_code": location[4]
    }
