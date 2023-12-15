from functools import wraps
import base64
import jwt
from flask import request, abort
from schema.authentication import Authentication


def authorization_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        from main import app
        token = None
        if "Authorization" in request.headers:
            token = request.headers["Authorization"].split(" ")[1]
        if not token:
            return {
                "message": "Authentication Token is missing!",
                "data": None,
                "error": "Unauthorized"
            }, 401
        try:
            data = jwt.decode(
                token, app.config["APP_SECRET"], algorithms=["HS256"])
            current_user = Authentication().get_user_by_id(data["user_id"])
            if current_user is None:
                return {
                    "message": "Invalid Authentication token!",
                    "data": None,
                    "error": "Unauthorized"
                }, 401
            if current_user.is_blocked:
                abort(403)
        except Exception as e:
            import traceback
            print(traceback.print_exc())
            return {
                "message": "Something went wrong",
                "data": None,
                "error": str(e)
            }, 500

        return f(current_user, *args, **kwargs)

    return decorated
