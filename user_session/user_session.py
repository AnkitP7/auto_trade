class user_session:
    def __init__(self):
        self.user_session_mapping = {}

    def add_user(self, user_id, kite_session):
        self.user_session_mapping[user_id] = kite_session

    def get_user(self, user_id):
        return self.user_session_mapping[user_id]

    def remover_user(self, user_id):
        self.user_session_mapping.pop(user_id)