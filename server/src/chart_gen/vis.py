from bokeh.io import curdoc
from bokeh.models.widgets import FileInput
from bokeh.models import ColumnDataSource, DataTable, TableColumn
from bokeh.layouts import column, row 
from bokeh.plotting import figure, show
from bokeh.transform import cumsum
from bokeh.palettes import Category20c
from bokeh.embed import components  
from bokeh.resources import CDN
from bokeh.embed import file_html, autoload_static



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

root = "server/src/ServerFiles"
file_IO = FileIO()
analysis = Analysis()




    

class Vis:
        

        def __init__(self):
                pass

        
        def to_file(self, html, name):
                with open(f'{name}.html', 'w') as f:
                        f.write(html)
                        f.close()




        def chart_to_html(self, p, name):
                html = file_html(p, CDN, name)
                self.to_file(html, name)




        def wordFreqChart(self, word_freq_dict):
                
                p = figure(x_range=list(word_freq_dict.keys()), plot_height=400,plot_width=600, title="Word Frequency", toolbar_location=None, tools="")
                p.vbar(x=list(word_freq_dict.keys()), top=list(word_freq_dict.values()), width=0.8)
                p.xaxis.major_label_orientation = "vertical"
                self.chart_to_html(p, "word_freq_chart")
                

        def letterFreqChart(self, letter_freq_dict):

                p = figure(x_range=list(letter_freq_dict.keys()), plot_height=400,plot_width=600, title="Letter Frequency", toolbar_location=None, tools="")
                p.vbar(x=list(letter_freq_dict.keys()), top=list(letter_freq_dict.values()), width=0.8)
                p.xaxis.major_label_orientation = "vertical"
                self.chart_to_html(p, "letter_freq_chart")


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

                self.chart_to_html(p, "win_chart")
                

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

                self.chart_to_html(p, "hardest_target_chart")

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

                self.chart_to_html(p, "easiest_target_chart")





        def elements(self):

                # Reading in string into wordle objects
                sample_string = FileIO.file_to_string("GlobalData.txt")
                games = file_IO.read(sample_string)

                # Doing analysis and making charts
                analysis.setGames(games)
                self.wordFreqChart(analysis.wordFreq())
                self.letterFreqChart(analysis.letterFreq())
                self.winChart(analysis.win_loss())
                self.hardestTargetChart(analysis.hardestTarget())
                self.easiestTargetChart(analysis.easiestTarget())

                # Adding charts to window
                
                
                
                
                
                





