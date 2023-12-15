from datetime import datetime
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from sqlalchemy.types import Time

db = SQLAlchemy()
ma = Marshmallow()


class User(db.Model):

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    is_blocked = db.Column(db.Boolean, nullable=False, default=False)
    login_attempts = db.Column(db.Integer, nullable=False, default=0)
    created_datetime = db.Column(
        db.DateTime, nullable=False, default=datetime.utcnow)

    def __repr__(self):
        return f'{self.username} - {self.email}'

class AutoConfig(db.Model):

    id = db.Column(db.Integer, primary_key=True)
    is_enabled = db.Column(db.Boolean, nullable=False, default=False)
    days_of_week = db.Column(db.Integer, nullable=False, default=1)
    index_name = db.Column(db.String(255), nullable=False,)
    tag = db.Column(db.String(50), nullable=True)
    start_time = db.Column(Time, nullable=False)
    end_time = db.Column(Time, nullable=False)
    quantity = db.Column(db.Integer, nullable=False)
    entry_criteria = db.Column(
        db.String(255), nullable=False, default='STRIKE_PRICE')
    entry_criteria_value = db.Column(db.Integer, nullable=False, default=0)
    stop_loss_type = db.Column(db.Boolean, nullable=False, default=True)
    stop_loss_value = db.Column(db.Integer, nullable=False, default=0)
    hedge = db.Column(db.Boolean, nullable=True, default=False)
    hedge_distance = db.Column(db.Integer, nullable=True, default=0)
    hedge_round_of_multiple = db.Column(db.Integer, nullable=True, default=100)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    created_datetime = db.Column(
        db.DateTime, nullable=False, default=datetime.utcnow)

    def __repr__(self):
        return f'User {self.user_id} - |Index {self.index_name}| Quantity {self.quantity}| Stop Loss {self.quantity}'


{
    "is_enabled": 1,
    "days_of_week": 1,
    "index_name": "NIFTY",
    "tag": "nifty_1",
    "start_time": "2023-12-12 9:30:30",
    "end_time": "2023-12-12 9:30:30",
    "quantity": 1,
    "entry_criteria": "MARKET_ENTER",
    "entry_critier_value": 1,
    "stop_loss_type": 1,
}
