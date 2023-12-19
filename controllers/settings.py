from flask import Blueprint, request, jsonify
from sqlalchemy import update
from helpers.authorize import authorization_required
from schema.settings import UserSettingsAPISchema, UserSettingsSchema
from models.app_models import UserSettings, db


settings = Blueprint('settings', __name__,)

@settings.route('/settings/get/', methods=['GET',])
@authorization_required
def get_settings(user):
    try:
        return jsonify({
            "status": True,
            "data": UserSettingsAPISchema().dump(UserSettings().query.filter_by(user_id=user.id).scalar()),
        }), 200
    except Exception as e:
        import traceback
        traceback.print_exc()
        return {'status': False, "error": {"code": "SE01", "message": "Exception occurred while retrieving settings"}}

@settings.route('/settings/edit/', methods=['POST',])
@authorization_required
def edit_settings(user):
    try:
        data = UserSettingsSchema().load(request.json)
        db.session.execute((update(UserSettings).where(UserSettings.user_id == user.id).values(**data)))
        db.session.commit()
        return jsonify({"status": True, "data": data, "error": {}})
    except Exception as e:
        import traceback
        print(traceback.print_exc())
    return {'status': False, "error": {"code": "SE02", "message": "Exception occurred while updating settings"}}
