from flask import Flask

app = Flask(__name__)


@app.route("parties/host")
def host_party():
    pass


@app.route("parties")
def get_recommended_parties():
    pass


@app.route("parties/<party_id>")
def get_party_details():
    pass


@app.route("parties/<party_id>/attend")
def attend_party(party_id):
    pass


@app.route("user/parties/attend")
def get_user_attend_parties():
    pass


@app.route("user/parties/host")
def get_user_host_parties():
    pass
