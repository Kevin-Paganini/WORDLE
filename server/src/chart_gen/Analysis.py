from Wordle import Wordle
from Score import Score


class Analysis:

    def __init__(self):
        
        self.games = []
        self.scores = []


    def wordFreq(self):
        master = self.makeMasterGuessList()
        word_freq = dict()
        for guess in master:
            if guess in word_freq:
                word_freq[guess] += 1
            else:
                word_freq[guess] = 1
        
        return self.top_50_words(word_freq)


    def top_50_words(self, markdict):
        marklist = sorted(markdict.items(), key=lambda x:x[1], reverse=True)
       
        if len(marklist) > 50:
            sortdict = dict(marklist[:50]) 
        else:
            sortdict = dict(marklist)
        
        return sortdict


    def sort_letters(self, markdict):
        marklist = sorted(markdict.items(), key=lambda x:x[1], reverse=True)
        sortdict = dict(marklist)
        return sortdict


    def win_loss(self):
        win_loss_dict = dict()
        win_loss_dict['WIN'] = 0
        win_loss_dict['LOSS'] = 0
        for game in self.games:
            if game.get_win() == True:
                win_loss_dict['WIN'] += 1
            else:
                win_loss_dict['LOSS'] += 1
        
        return win_loss_dict
        
    def num_guesses(self):
        num_guess_dict = dict()
        num_guess_dict['1'] = 0
        num_guess_dict['2'] = 0
        num_guess_dict['3'] = 0
        num_guess_dict['4'] = 0
        num_guess_dict['5'] = 0 
        num_guess_dict['6'] = 0 
        for game in self.games:
            if game.get_num_guesses() == '1':
                num_guess_dict['1'] += 1
            elif game.get_num_guesses() == '2':
                num_guess_dict['2'] += 1
            elif game.get_num_guesses() == '3':
                num_guess_dict['3'] += 1
            elif game.get_num_guesses() == '4':
                num_guess_dict['4'] += 1
            elif game.get_num_guesses() == '5':
                num_guess_dict['5'] += 1
            elif game.get_num_guesses() == '6':
                num_guess_dict['6'] += 1
        return num_guess_dict


    
    def setGames(self, games):
        self.games = games

    def setScores(self, scores):
        self.scores = scores

    def make_scores(self):
        leaderboard = []
        
        for score in self.scores:
            
            if score.num_letters == 5 and score.suggestions == 0 and score.hard == 0:
                leaderboard.append(score)
        
        leaderboard = sorted(leaderboard, key=lambda x: x.time, reverse=False)
        
        return leaderboard

    def letterFreq(self):
        master = self.makeMasterGuessList()
        letter_freq = dict()
        for guess in master:
            for letter in guess:
                if letter in letter_freq:
                    letter_freq[letter] += 1

                else:
                    letter_freq[letter] = 1

        return self.sort_letters(letter_freq)



    def hardestTarget(self):
        hard_Target = dict()
        for game in self.games:
            if game.win == False:
                if game.get_target() in hard_Target:
                    hard_Target[game.get_target()] += 1
                else:
                    hard_Target[game.get_target()] = 1 
        
        return self.sort_targets(hard_Target)

    def easiestTarget(self):
        easy_Target = dict()
        for game in self.games:
            if game.win == True:
                if game.get_target() in easy_Target:
                    easy_Target[game.get_target()] += 1 
                else:
                    easy_Target[game.get_target()] = 1

        
        return self.sort_targets(easy_Target)

    


    def sort_targets(self, hard_Target):
        marklist = sorted(hard_Target.items(), key=lambda x:x[1], reverse=True)
        if len(marklist) > 10:
            sortdict = dict(marklist[:10])
        else:
            sortdict = dict(marklist)
        return sortdict

    def makeMasterGuessList(self):
        master_guess_list = []
        for game in self.games:
            master_guess_list.extend(game.get_guess_list())
        return master_guess_list
