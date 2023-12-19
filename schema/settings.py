from marshmallow import Schema, fields, EXCLUDE
from models.app_models import UserSettings, User
from models.app_models import ma

class UserSchema(Schema):

    class Meta:
        fields = ['username', 'email']


class UserSettingsSchema(Schema):

    trade_user_id = fields.Str(required=True)
    trade_api_key = fields.Str(required=True)
    trade_vendor = fields.Str(required=True)
    is_verified = fields.Bool(required=True)
    is_active = fields.Bool(required=True)

    class Meta:
        unknown = EXCLUDE


class UserSettingsAPISchema(ma.SQLAlchemyAutoSchema):

    user = ma.Nested(UserSchema())

    class Meta:
        model = UserSettings
        # include_fk = True
        load_instance = True
        load_relationships = True
