from Wordle import Wordle

DEBUG = False

class FileIO:
    


    def file_to_string(filename):
        with open(filename) as f:
            ret = f.read()
            f.close()
        
        return ret 


        
    
        


    def read(self, file_contents):
        
        games_list = []
        games = file_contents.split("\n\n\n")
        
        for game in games:
            split_game = game.split("\n")
            if len(split_game) > 5:

                
                user = split_game[0].split(": ")[1].strip()
                game_num = split_game[1].split(": ")[1].strip()
                target = split_game[2].split(": ")[1].strip()
                num_guesses = split_game[3].split(": ")[1].strip()
                win = split_game[4].split(": ")[1].strip()
                if win == "Yes":
                    win = True
                else:
                    win = False
                
                guesses_dirty = game.split("\n", 5)[5].split("\n")
                guess_clean = []
                for guess in guesses_dirty:
                    if guess.strip() != "":
                        guess_clean.append(guess.strip())


                wordle = Wordle(user=user, game_number=game_num, target=target, number_guesses=num_guesses, win=win, guess_list=guess_clean)

                if (DEBUG):

                    print(f'{user} {game_num} {target} {num_guesses} {win}')
                    print(f"{guess_clean}")
                    wordle.prettyPrint()

                
                
                games_list.append(wordle)

            
        return games_list




            
            