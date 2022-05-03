class Score:

    def __init__(self, user, time,
                 num_guesses, num_letters, hard, suggestions):
        self.user = user
        self.time = time
        self.num_guesses = num_guesses
        self.num_letters = num_letters
        self.hard = hard
        self.suggestions = suggestions

    def get_user(self):
        return self.user

    def get_time(self):
        return self.time

    def get_num_guesses(self):
        return self.num_guesses

    def get_num_letters(self):
        return self.num_letters

    def get_hard(self):
        return self.hard

    def get_suggestions(self):
        return self.suggestions

    def prettyPrint(self):
        print(
            f'User: {self.user}\nTime: {self.time}\nnum_guesses: {self.num_guesses}\nNum Letters: {self.num_letters}\nHard: {self.hard}\nSuggestions: {self.suggestions}')
        print("\n")