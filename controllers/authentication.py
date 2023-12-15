from datetime import datetime, timezone
from flask import Blueprint
from flask import request
import jwt
from hashlib import sha512
from helpers.kite_connect import KiteConnectHelper
from schema.authentication import AuthenticationSchema, AuthValidationError
from schema.authentication import Authentication


authentication = Blueprint('authentication', __name__,)
kite_helper = None


@authentication.route('/internal/login/', methods=['POST'])
def authenticate():
    try:
        from main import app
        data = request.json
        # validate input
        authentication_schema = AuthenticationSchema().load(data)
        user = Authentication().authenticate(
            authentication_schema['email'], authentication_schema['password'])
        data = {}
        if user:
            try:
                # token should expire after 24 hrs
                data["token"] = jwt.encode(
                    {"user_id": user.id},
                    app.config["APP_SECRET"],
                    algorithm="HS256",
                )
                data["username"] = user.username
                return {
                    "status": True,
                    "data": data
                }
            except Exception as e:
                return {
                    "status": False,
                    "error": {
                        "message": "Please try again. Unable to login with the given credentials",
                        "code": "AUTHE01"
                    }
                }, 500
        return {
            "status": False,
            "error": {
                "message": "Invalid email or password. Please check your credentials and try again",
                "code": "AUTHE02"
            }
        }, 400
    except Exception as e:
        import traceback
        traceback.print_exc()
        if isinstance(e, AuthValidationError):
            return e.messages, 401

        return {
            "status": False,
            "error":{
                "message": "Please try again. Unable to login with the given credentials",
                "code": "AUTHE03",
            }
        }, 400


@authentication.route("/connect/login/", methods=['GET'])
def login():
    """
    The login function creates an instance of KiteConnectHelper and connects to the API using the
    provided API key.
    :return: The login function is returning the result of the `_connect()` method of the `kite_helper`
    object.
    """
    kite_helper = KiteConnectHelper()
    return kite_helper._connect()


@authentication.route("/connect/login/request/<data>")
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
    return {"message": "Exception occurred::Kite connect not found", "status": False}
