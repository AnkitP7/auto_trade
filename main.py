from flask import Flask
from dotenv import dotenv_values
from flask_cors import CORS
from models.app_models import db, ma
from flask_migrate import Migrate  
from controllers.authentication import authentication
from controllers.configuration import configuration

def _load_extensions(app):
    app.config.update(dotenv_values())
    db.init_app(app)
    migrate = Migrate(app, db)
    app.register_blueprint(authentication)
    app.register_blueprint(configuration)
    if app.config.get('APP_ENV') != 'prod':
        cors = CORS(app)
    ma.init_app(app)
    return app

def create_app():
    app = Flask(__name__, static_folder='static', template_folder='static')
    return _load_extensions(app)

app = create_app()