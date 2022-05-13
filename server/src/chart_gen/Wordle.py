


class Wordle:
 



    def __init__(self, user, game_number,
                target, number_guesses, win, guess_list):

        self.user = user
        self.guess_list = guess_list
        self.target = target
        self.win = win
        self.game_number = game_number
        self.number_guesses = number_guesses


    
    def get_game_number(self):
        return self.game_number
    
    def get_guess_list(self):
        return self.guess_list

    def get_win(self):
        return self.win
    
    def get_num_guesses(self):
        return self.number_guesses


    def get_user(self):
        return self.user

    def get_target(self):
        return self.target


    def prettyPrint(self):
        print(f'User: {self.user}\nGame No.: {self.game_number}\nTarget: {self.target}\nNum Guesses: {self.number_guesses}\nWin: {self.win}\n')
        for guess in self.guess_list:
            print(guess)

        print("\n")

        

    
