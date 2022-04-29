from bokeh.io import curdoc
from bokeh.models.widgets import FileInput
from bokeh.models import ColumnDataSource, DataTable, TableColumn
from bokeh.layouts import column, row 
from bokeh.plotting import figure, show
from bokeh.transform import cumsum
from bokeh.palettes import Category20c
from bokeh.embed import components  
from bokeh.resources import CDN


import base64
import io
from numpy.lib.utils import source
import pandas as pd
from pandas.io.pytables import Table
from math import pi


from FileIO import FileIO
from Analysis import Analysis




#############################################################
# https://www.youtube.com/watch?v=VFpfZz6w9Oo
# Bokeh Bar charts
#############################################################
# Idea on how to keep getting input in and updating
# http://matthewrocklin.com/blog/work/2017/06/28/simple-bokeh-server
#############################################################

file_IO = FileIO()
analysis = Analysis()




file_input = FileInput(accept=".txt", width=400)




    

class Vis:
        

        def __init__(self):
                pass




        def wordFreqChart(self, word_freq_dict):
                
                p = figure(x_range=list(word_freq_dict.keys()), plot_height=400,plot_width=600, title="Word Frequency", toolbar_location=None, tools="")
                p.vbar(x=list(word_freq_dict.keys()), top=list(word_freq_dict.values()), width=0.8)
                p.xaxis.major_label_orientation = "vertical"
                return p

        def letterFreqChart(self, letter_freq_dict):

                p = figure(x_range=list(letter_freq_dict.keys()), plot_height=400,plot_width=600, title="Letter Frequency", toolbar_location=None, tools="")
                p.vbar(x=list(letter_freq_dict.keys()), top=list(letter_freq_dict.values()), width=0.8)
                p.xaxis.major_label_orientation = "vertical"
                return p


        def winChart(self, x):
                chart_colors = ['#00d084', '#b71c1c', '#e244db',
                                '#d8e244', '#eeeeee', '#56e244', '#007bff', 'black', 'green', 'blue']
                data = pd.Series(x).reset_index(name='value').rename(columns={'index': 'country'})
                data['angle'] = data['value']/data['value'].sum() * 2*pi
                data['color'] = chart_colors[:len(x)]

                p = figure(height=350, title="Win / Loss Chart", toolbar_location=None,
                        tools="hover", tooltips="@country: @value", x_range=(-0.5, 1.0))

                p.wedge(x=0, y=1, radius=0.4,
                        start_angle=cumsum('angle', include_zero=True), end_angle=cumsum('angle'),
                        line_color="white", fill_color='color', legend_field='country', source=data)

                p.axis.axis_label = None
                p.axis.visible = False
                p.grid.grid_line_color = None

                return p
                

        def hardestTargetChart(self, x):
                chart_colors = ['#00d084', '#b71c1c', '#e244db',
                                '#d8e244', '#eeeeee', '#56e244', '#007bff', 'black', 'green', 'blue']
                data = pd.Series(x).reset_index(name='value').rename(columns={'index': 'country'})
                data['angle'] = data['value']/data['value'].sum() * 2*pi
                data['color'] = chart_colors[:len(x)]

                p = figure(height=350, title="Targets with most losses", toolbar_location=None,
                        tools="hover", tooltips="@country: @value", x_range=(-0.5, 1.0))

                p.wedge(x=0, y=1, radius=0.4,
                        start_angle=cumsum('angle', include_zero=True), end_angle=cumsum('angle'),
                        line_color="white", fill_color='color', legend_field='country', source=data)

                p.axis.axis_label = None
                p.axis.visible = False
                p.grid.grid_line_color = None

                return p

        def easiestTargetChart(self, x):
                chart_colors = ['#00d084', '#b71c1c', '#e244db',
                                '#d8e244', '#eeeeee', '#56e244', '#007bff', 'black', 'green', 'blue']
                data = pd.Series(x).reset_index(name='value').rename(columns={'index': 'country'})
                data['angle'] = data['value']/data['value'].sum() * 2*pi
                data['color'] = chart_colors[:len(x)]

                p = figure(height=350, title="Targets with most wins", toolbar_location=None,
                        tools="hover", tooltips="@country: @value", x_range=(-0.5, 1.0))

                p.wedge(x=0, y=1, radius=0.4,
                        start_angle=cumsum('angle', include_zero=True), end_angle=cumsum('angle'),
                        line_color="white", fill_color='color', legend_field='country', source=data)

                p.axis.axis_label = None
                p.axis.visible = False
                p.grid.grid_line_color = None

                return p





        def elements(self):

                # Reading in string into wordle objects
                sample_string = FileIO.file_to_string("GlobalData.txt")
                games = file_IO.read(sample_string)

                # Doing analysis and making charts
                analysis.setGames(games)
                word_chart = components(self.wordFreqChart(analysis.wordFreq()))
                letter_chart = components(self.letterFreqChart(analysis.letterFreq()))
                win_chart = components(self.winChart(analysis.win_loss()))
                hardest_target = components(self.hardestTargetChart(analysis.hardestTarget()))
                easiest_target = components(self.easiestTargetChart(analysis.easiestTarget()))

                # Adding charts to window
                
                
                cdn_js = CDN.js_files[0]
                
                
                return [word_chart, letter_chart, win_chart, hardest_target, easiest_target, cdn_js]





