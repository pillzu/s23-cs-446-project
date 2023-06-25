
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


def row_to_location(location):
    return {
        "party_id": location[0],
        "street": location[1],
        "city": location[2],
        "prov": location[3],
        "postal_code": location[4]
    }
