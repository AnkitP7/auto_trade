from hashlib import sha512
from sqlalchemy import select
from marshmallow import Schema, fields, ValidationError as AuthValidationError
# from .configuration import marsh_mw
from models.app_models import db, User



class AuthenticationSchema(Schema):

    email = fields.Email(required=True)
    password = fields.Str(required=True)


{
    "email": "ankit.patel39@gmail.com",
    "password": "Test@123"
}


class Authentication:

    def generate_password(self, password):
        return sha512(password.encode('utf-8')).hexdigest()

    def authenticate(self, email, password):
        try:
            _user = db.session().execute(select(User).where(
                User.email == email, User.is_blocked == False)).scalar_one()
            if _user and _user.password == self.generate_password(password):
                return _user
        except Exception as e:
            pass

    def get_user_by_id(self, id):
        return db.session().execute(select(User).where(User.id == id, User.is_blocked == False)).scalar_one()
