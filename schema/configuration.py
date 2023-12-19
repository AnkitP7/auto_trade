from marshmallow import Schema, fields, EXCLUDE
from models.app_models import AutoConfig
from models.app_models import ma


class ConfigurationSchema(Schema):

    tag = fields.Str(required=False, attribute='tag')
    indexName = fields.Str(required=True, attribute='index_name')
    daysOfWeek = fields.Int(required=True, attribute='days_of_week')
    formattedStartTime = fields.Str(
        required=True, format='%H:%M:%S%z', attribute='start_time')
    formattedEndTime = fields.Str(
        required=True, format='%H:%M:%S%z', attribute='end_time')
    quantity = fields.Int(required=True, attribute='quantity')
    entryCriteria = fields.Str(required=True, attribute='entry_criteria')
    entryCriteriaValue = fields.Float(
        required=True, attribute='entry_criteria_value')
    stopLossType = fields.Bool(required=True, attribute='stop_loss_type')
    stopLossValue = fields.Float(required=True, attribute='stop_loss_value')
    isEnabled = fields.Bool(required=True, attribute='is_enabled')
    hedge = fields.Bool(required=False, attribute='hedge',
                        missing=False, default='')
    hedgeDistance = fields.Int(
        required=False, attribute='hedge_distance', missing=0, default=0)
    hedgeRoundOfMultiple = fields.Int(
        required=False, attribute='hedge_round_of_multiple', missing=0, default=0)

    class Meta:
        unknown = EXCLUDE


class Configuration(ma.SQLAlchemyAutoSchema):

    class Meta:
        model = AutoConfig
        include_fk = True
        load_instance = True
        fields = ('id', 'tag', 'indexName', 'daysOfWeek', 'startTime', 'endTime', 'quantity',
                  'entryCriteria', 'entryCriteriaValue', 'stopLossType', 'stopLossValue', 'isEnabled', 'hedge', 'hedgeDistance', 'hedgeRoundOfMultiple')

    tag = ma.auto_field("tag")
    indexName = ma.auto_field('index_name', dump_only=True)
    daysOfWeek = ma.auto_field('days_of_week', dump_only=True)
    startTime = ma.auto_field('start_time', dump_only=True)
    endTime = ma.auto_field('end_time', dump_only=True)
    entryCriteria = ma.auto_field('entry_criteria', dump_only=True)
    entryCriteriaValue = ma.auto_field('entry_criteria_value', dump_only=True)
    stopLossType = ma.auto_field('stop_loss_type', dump_only=True)
    stopLossValue = ma.auto_field('stop_loss_value', dump_only=True)
    isEnabled = ma.auto_field('is_enabled', dump_only=True)
    hedge = ma.auto_field('hedge', dump_only=True)
    hedgeDistance = ma.auto_field('hedge_distance', dump_only=True)
    hedgeRoundOfMultiple = ma.auto_field(
        'hedge_round_of_multiple', dump_only=True)
