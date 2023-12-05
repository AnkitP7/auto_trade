from flask import Flask
from dotenv import dotenv_values
from helpers.kite_connect import KiteConnectHelper

app = Flask(__name__, static_folder='static', template_folder='static')
app.config.update(dotenv_values())

kite_helper = None

# @app.route("/")
# def main():
#     return render_template('index.html')

@app.route("/connect/login/")
def login():
    """
    The login function creates an instance of KiteConnectHelper and connects to the API using the
    provided API key.
    :return: The login function is returning the result of the `_connect()` method of the `kite_helper`
    object.
    """
    kite_helper = KiteConnectHelper(app['API_KEY'])
    return kite_helper._connect()

@app.route("/connect/login/request/<data>")
def login_callback(data):
    """
    The function `login_callback` checks if `kite_helper` is available and calls its `_session` method
    with the provided `data`, otherwise it returns an error message.
    
    :param data: The `data` parameter is the input data that is passed to the `login_callback` function.
    It is used as an argument when calling the `_session` method of the `kite_helper` object
    :return: a dictionary with two key-value pairs. The "message" key has the value "Exception
    occurred::Kite connect not found" and the "status" key has the value False.
    """
    if kite_helper:
        return kite_helper._session(data)
    return {"message": "Exception occurred::Kite connect not found", "status": False }