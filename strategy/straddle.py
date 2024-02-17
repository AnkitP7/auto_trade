import logging

from helpers.trader import generate_uuid
from strategy.strategy import strategy
from helpers import trader
from datetime import datetime

class straddle(strategy):

    def __init__(self, strategy_config):
        super().__init__()
        self.day_to_execute = strategy_config['days_of_week']
        self.index_config = get_index_config()
        self.enabled = strategy_config['is_enabled']
        self.underlying_instrument = strategy_config['index_name']
        self.start_time = strategy_config['start_time']
        self.end_time = strategy_config['end_time']
        self.quantity = strategy_config['quantity']
        self.stop_loss_type = strategy_config['stop_loss_type']
        self.stop_loss_value = strategy_config['stop_loss_value']
        self.money_ness = strategy_config['money_ness']

    def execute_strategy(self):
        underlying_ltp = trader.get_ltp(self.underlying_instrument)
        atm_strike_price = round(underlying_ltp / self.index_config[self.underlying_instrument]) * self.index_config[
            self.underlying_instrument]
        call_strike_price = atm_strike_price + self.money_ness * self.index_config[self.underlying_instrument]
        put_strike_price = atm_strike_price - self.money_ness * self.index_config[self.underlying_instrument]
        # Populate this in a mapping in startup
        closest_expiry = trader.get_closest_expiry(self.underlying_instrument)
        stoploss_factor = self.stop_loss_value

        call_ticker = trader.calculate_symbol_to_be_traded(ticker=self.underlying_instrument, expiry=closest_expiry,
                                                           type="CE",
                                                           strike_price=call_strike_price)
        put_ticker = trader.calculate_symbol_to_be_traded(ticker=self.underlying_instrument, expiry=closest_expiry,
                                                          type="PE",
                                                          strike_price=put_strike_price)
        call_order_tag = generate_uuid()
        call_order_id = trader.place_market_order(ticker=call_ticker, quantity=self.quantity,
                                                  transaction_type="SELL", transaction_id=call_order_tag)
        put_order_tag = generate_uuid()
        put_order_id = trader.place_market_order(ticker=put_ticker, quantity=self.quantity,
                                                 transaction_type="SELL", transaction_id=put_order_tag)
        check_call_order_status = trader.fetch_order_details(call_order_id)
        check_put_order_status = trader.fetch_order_details(put_order_id)
        if (check_call_order_status != None):
            logging.info(
                "{0} call order with id {1} successfully executed for {2} at {3}".format(self.user_id, call_order_tag,
                                                                                         call_ticker,
                                                                                         check_call_order_status[
                                                                                             'average_price']))
            call_stop_loss_price = float(check_call_order_status['average_price'] * float(1 + stoploss_factor))
            call_stop_loss_tag = generate_uuid()
            call_stoploss_order_id = trader.place_limit_order(ticker=call_ticker, quantity=self.quantity,
                                                              price=call_stop_loss_price, transaction_type="BUY",
                                                              transaction_id=call_stop_loss_tag)
            self.call_stop_loss_order_id = call_stoploss_order_id

            if(call_stoploss_order_id != None):
                logging.info(
                    "{0} stoploss order with id {1} for {2} successfully placed  at {3}".format(self.user_id,
                                                                                             call_stop_loss_tag,
                                                                                             call_ticker,
                                                                                             datetime.now()))

        if (check_put_order_status != None):
            logging.info(
                "{0} put order with id {1} successfully executed for {2} at {3}".format(self.user_id, put_order_tag,
                                                                                         put_ticker,
                                                                                         check_put_order_status[
                                                                                             'average_price']))
            put_stop_loss_price = float(check_put_order_status['average_price'] * float(1 + stoploss_factor))
            put_stop_loss_tag = generate_uuid()
            put_stoploss_order_id = trader.place_limit_order(ticker=put_ticker, quantity=self.quantity,
                                                              price=put_stop_loss_price, transaction_type="BUY",
                                                              transaction_id=put_stop_loss_tag)
            self.put_stop_loss_order_id = put_stoploss_order_id

            if(put_stoploss_order_id != None):
                logging.info(
                    "{0} stoploss order with id {1} for {2} successfully placed  at {3}".format(self.user_id,
                                                                                             put_stop_loss_tag,
                                                                                             put_ticker,
                                                                                             datetime.now()))
