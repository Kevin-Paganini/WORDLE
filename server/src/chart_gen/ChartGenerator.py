from Wordle import Wordle
from Analysis import Analysis
from FileIO import FileIO
from vis import Vis
from bs4 import BeautifulSoup


# Calls vis to generate new html charts
# parses start of file with body from each chart
# puts end of file on to html file
# Practically making a sandwich 
# index_template_start
# Body off charts x5
#index_template_end
# Generates index_test.html
def generateCharts():
    chartGen = Vis()
    chartGen.elements()
    
    #paste this first
    file_index_start = open("chart_gen/index_template_start.txt", mode="r")
    index_start = file_index_start.read()
    #paste this second
    file_index_end = open("chart_gen/index_template_end.txt", mode="r")
    index_end = file_index_end.read()
    
    ht_html = open("chart_gen/hardest_target_chart.html").read()
    ht_soup = BeautifulSoup(ht_html, features='html.parser')
    ht_soup = ht_soup.find('body')
    
    et_html = open("chart_gen/easiest_target_chart.html").read()
    et_soup = BeautifulSoup(et_html, features='html.parser')
    et_soup = et_soup.find('body')

    lf_html = open("chart_gen/letter_freq_chart.html").read()
    lf_soup = BeautifulSoup(lf_html, features='html.parser')
    lf_soup = lf_soup.find('body')

    wc_html = open("chart_gen/win_chart.html").read()
    wc_soup = BeautifulSoup(wc_html, features='html.parser')
    wc_soup = wc_soup.find('body')

    wfc_html = open("chart_gen/word_freq_chart.html").read()
    wfc_soup = BeautifulSoup(wfc_html, features='html.parser')
    wfc_soup = wfc_soup.find('body')

    # lb_html = open("chart_gen/leaderboard_chart.html").read()
    # lb_soup = BeautifulSoup(lb_html, features='html_parser')
    # lb_soup = lb_soup.find('body')

    with open("chart_gen/index_test.html", "w") as main_file:
        main_file.write(index_start)
        main_file.write(str(ht_soup) + "\n")
        main_file.write("</div>\n<div class=\"Statistics\">\n")
        main_file.write(str(et_soup) + "\n")
        main_file.write("</div>\n<div class=\"Statistics\">\n")
        main_file.write(str(lf_soup) + "\n")
        main_file.write("</div>\n<div class=\"Statistics\">\n")
        main_file.write(str(wc_soup) + "\n")
        main_file.write("</div>\n<div class=\"Statistics\">\n")
        main_file.write(str(wfc_soup) + "\n")
        # main_file.write("</div>\n<div class=\"Statistics\">\n")
        # main_file.write(str(wfc_soup) + "\n")

        main_file.write(index_end)


    






if __name__ == "__main__":
    generateCharts()