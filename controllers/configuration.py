from marshmallow import ValidationError
import traceback
from flask import Blueprint, request, jsonify
from sqlalchemy import update
from models.app_models import AutoConfig, db
from schema.configuration import ConfigurationSchema, Configuration
from helpers.authorize import authorization_required

configuration = Blueprint('configuration', __name__,)


@configuration.route(f"/configuration/create/", methods=['POST'])
@authorization_required
def create_configuration(data):
    try:
        configuration_schema = ConfigurationSchema().load(request.json)
        configuration_schema['user_id'] = data.id
        db.session.add(AutoConfig(**configuration_schema))
        db.session.commit()
        return {"status": True, "data": configuration_schema, "error": {}}, 200
    except Exception as e:
        traceback.print_exc()
        if isinstance(e, ValidationError):
            return jsonify({"status": False, "error": {"message": e.messages, "code": "COCREATE2"}}), 400
        return jsonify({"status": False, "error": {"message": "Exception occurred::COCREATE1", "code": "COCREATE1"}})


@configuration.route(f"/configuration/edit/", methods=['PUT'])
@authorization_required
def edit_configuration(data):
    try:
        payload = request.json
        configuration_schema = ConfigurationSchema().load(payload)
        print(configuration_schema)
        db.session.execute((update(AutoConfig).where(AutoConfig.id ==
                       payload['id']).values(**configuration_schema)))
        db.session.commit()
        return {"status": True, "data": configuration_schema, "error": {}}, 200
    except Exception as e:
        traceback.print_exc()
        if isinstance(e, ValidationError):
            return jsonify({"status": False, "error": {"message": e.messages, "code": "COE3"}}), 400
        return jsonify({"status": False, "error": {"message": "Exception occurred::COCEDIT1", "code": "COCEDIT1"}})


@configuration.route("/configuration/get/", methods=['GET'])
@authorization_required
def get_configuration(data):
    try:
        return jsonify({
            'status': True,
            'data': Configuration().dump(AutoConfig().query.filter_by(user_id=data.id).all(), many=True)
        }), 200
    except Exception as e:
        traceback.print_exc()
        return jsonify({"status": False, "error": {"message": "Exception occurred::COGET1", "code": "COGET1"}})
