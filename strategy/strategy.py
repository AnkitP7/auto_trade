import logging
from abc import ABC, abstractmethod
from datetime import datetime

from helpers import trader


class strategy(ABC):

    def __init__(self, user_id):
        self.enabled = False
        self.user_id = user_id
        self.call_stop_loss_order_id = None
        self.put_stop_loss_order_id = None

    @abstractmethod
    def execute_strategy(self):
        pass

    @abstractmethod
    def run_strategy(self):
        if (self.enabled):
            self.execute_strategy()

    def exit(self):
        # need to add the time check here
        if (self.call_stop_loss_order_id != None):
            convert_call_stop_loss_to_market_order_id = trader.generate_uuid()
            trader.modify_order_to_market(order_id=self.call_stop_loss_order_id,
                                          transaction_id=convert_call_stop_loss_to_market_order_id)
            logging.info(
                "{0} stoploss order with zerodha id {1} converted to {2} successfully placed  at {3}".format(self.user_id,
                                                                                            self.call_stop_loss_order_id,
                                                                                            convert_call_stop_loss_to_market_order_id,
                                                                                            datetime.now()))
        if (self.call_stop_loss_order_id != None):
            convert_call_stop_loss_to_market_order_id = trader.generate_uuid()
            trader.modify_order_to_market(order_id=self.call_stop_loss_order_id,
                                          transaction_id=convert_call_stop_loss_to_market_order_id)
            logging.info(
                "{0} stoploss order with zerodha id {1} converted to {2} successfully placed  at {3}".format(self.user_id,
                                                                                            self.call_stop_loss_order_id,
                                                                                            convert_call_stop_loss_to_market_order_id,
                                                                                            datetime.now()))
