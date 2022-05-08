from Wordle import Wordle
from Score import Score

DEBUG = False

class FileIO:
    


    def file_to_string(filename):
        with open(filename) as f:
            ret = f.read()
            f.close()
        
        return ret 


        
    
        


    def read(self, file_contents):
        
        games_list = []
        games = file_contents.split("\n\n")
        
        for game in games:
            split_game = game.split("\n")
            if len(split_game) > 5:

                
                user = split_game[1].split("=")[1].strip()
                game_num = split_game[5].split("=")[1].strip()
                target = split_game[0].split("=")[1].strip()
                num_guesses = split_game[3].split("=")[1].strip()
                win = split_game[4].split("=")[1].strip()
                if win == "Yes":
                    win = True
                else:
                    win = False

                guess_clean = []
                guesses_dirty = split_game[2].split("=")[1].strip()
                print(guesses_dirty)
                guesses_dirty = guesses_dirty.split("+")
                for guess in guesses_dirty:

                    guess = guess.strip()
                    if guess != "":
                        guess_clean.append(guess)

                print(guess_clean)
                wordle = Wordle(user=user, game_number=game_num, target=target, number_guesses=num_guesses, win=win, guess_list=guess_clean)

                if (DEBUG):

                    print(f'{user} {game_num} {target} {num_guesses} {win}')
                    print(f"{guess_clean}")
                    wordle.prettyPrint()

                
                
                games_list.append(wordle)

            
        return games_list

    def readScoreboard(self, file_contents):

        scores_list = []
        scores = file_contents.split("\n\n")

        for score in scores:
            split_score = score.split("\n")
            if len(split_score) > 5:

                user = split_score[0].split("=")[1].strip()
                time = split_score[1].split("=")[1].strip()
                num_time = float(time)
                num_guesses = split_score[2].split("=")[1].strip()
                num_letters = split_score[3].split("=")[1].strip()
                hard = split_score[4].split("=")[1].strip()
                num_hard = int(hard)
                suggestions = split_score[5].split("=")[1].strip()
                num_suggestions = int(suggestions)

                score = Score(user, num_time, num_guesses, num_letters, num_hard, num_suggestions)
                if (DEBUG):
                    print(f'{user} {time} {num_guesses} {num_letters} {hard}')
                    print(f"{suggestions}")
                    score.prettyPrint()

                scores_list.append(score)

        return scores_list




            
            
