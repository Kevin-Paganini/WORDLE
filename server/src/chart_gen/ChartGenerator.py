from Wordle import Wordle
from Analysis import Analysis
from FileIO import FileIO
from vis import Vis
from bs4 import BeautifulSoup



def generateCharts():
    chartGen = Vis()
    chartGen.elements()
    
    #paste this first
    file_index_start = open("index_template_start.txt", mode="r")
    index_start = file_index_start.read()
    #paste this second
    file_index_end = open("index_template_end.txt", mode="r")
    index_end = file_index_end.read()
    
    ht_html = open("hardest_target_chart.html")
    et_html = open("easiest_target_chart.html")
    lf_html = open("letter_freq_chart.html")
    wc_html = open("win_chart.html")
    wfc_html = open("word_freq_chart.html")

    ht_soup = BeautifulSoup(ht_html, 'html.parser').b.string
    et_soup = BeautifulSoup(et_html, 'html.parser').b.string
    lf_soup = BeautifulSoup(lf_html, 'html.parser').b.string
    wc_soup = BeautifulSoup(wc_html, 'html.parser').b.string
    wfc_soup = BeautifulSoup(wfc_html, 'html.parser').b.string



    with open("index.html", "w") as main_file:
        main_file.write(file_index_start)
        main_file.write(ht_soup + "\n")
        main_file.write(et_soup + "\n")
        main_file.write(lf_soup + "\n")
        main_file.write(wc_soup + "\n")
        main_file.write(wfc_soup + "\n")
        main_file.write(index_end)

    






if __name__ == "__main__":
    generateCharts()