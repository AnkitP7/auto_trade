from flask import Flask
from dotenv import dotenv_values
from helpers.kite_connect import KiteConnectHelper

app = Flask(__name__, static_folder='static', template_folder='static')
env_config = dotenv_values()
kite_helper = None

# @app.route("/")
# def main():
#     return render_template('index.html')

@app.route("/connect/login/")
def login():
    kite_helper = KiteConnectHelper(env_config['APP_TOKEN'])
    return kite_helper._connect()
    # return KiteConnectHelper(env_config['APP_TOKEN'])._connect()

@app.route("/connect/login/callback/<data>")
def login_callback(data):
    if kite_helper:
        return kite_helper._session(data)
    return {"message": "Exception occurred::Kite connect not found", "status": False }