import { Component, OnInit } from "@angular/core";
import * as Chart from "chart.js";

import { HttpClient } from '@angular/common/http';


@Component({
  selector: "app-dashboard",
  templateUrl: "dashboard.component.html"
})
export class DashboardComponent implements OnInit {
  test: Date = new Date();
  public canvas : any;
  public ctx;
  public data: any;
  public myChartData;
  public clicked: boolean = true;
  public clicked1: boolean = false;
  public clicked2: boolean = false;

  public count: number;
  public pro: number;
  public no: number;
  public tweets: any[] = [];

  public who: string = "ProVax";
  public how: number = 0;

  public datasetPro: any;
  public datasetNo: any;

  public datasetProValues: any[] = [0];
  public datasetNoValues: any[] = [0];

  public labelsPro: any[] = ["00:00"];
  public labelsNo: any[] = ["00:00"];

  public chart_labels: any;

  date: Date = new Date();
  
  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.http.get<any>("http://localhost:8080/dashboard/tweets").subscribe(res => {
        this.tweets = res;
        this.count = this.tweets.length;
        this.pro = this.tweets.map((get) => get.sentiment).filter((v, i, a) => v == "Positive").length;
        this.no = this.tweets.map((get) => get.sentiment).filter((v, i, a) => v == "Negative").length;
        this.how = this.pro;

        this.datasetPro = this.getTimeSum(this.tweets, "Positive");
        this.datasetNo = this.getTimeSum(this.tweets, "Negative");

        
        if (Object.keys(this.datasetPro).length !== 0){
          this.labelsPro = Object.keys(this.datasetPro);
          this.datasetProValues = Object.values(this.datasetPro);
        }
        if (Object.keys(this.datasetNo).length !== 0){
          this.labelsNo = Object.keys(this.datasetNo);
          this.datasetNoValues = Object.values(this.datasetNo);
        }
      });

    var gradientChartOptionsConfigurationWithTooltipRed: any = {
      maintainAspectRatio: false,
      legend: {
        display: false
      },

      tooltips: {
        backgroundColor: '#f5f5f5',
        titleFontColor: '#333',
        bodyFontColor: '#666',
        bodySpacing: 4,
        xPadding: 12,
        mode: "nearest",
        intersect: 0,
        position: "nearest"
      },
      responsive: true,
      scales: {
        yAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(29,140,248,0.0)',
            zeroLineColor: "transparent",
          },
          ticks: {
            suggestedMin: 60,
            suggestedMax: 125,
            padding: 20,
            fontColor: "#9a9a9a"
          }
        }],

        xAxes: [{
          barPercentage: 1.6,
          gridLines: {
            drawBorder: false,
            color: 'rgba(233,32,16,0.1)',
            zeroLineColor: "transparent",
          },
          ticks: {
            padding: 20,
            fontColor: "#9a9a9a"
          }
        }]
      }
    };

    /*------------------------- START BIG CHART -------------------------*/
    this.chart_labels = this.labelsPro;
    this.data = this.datasetPro;

    this.canvas = document.getElementById("chartBig1");
    this.ctx = this.canvas.getContext("2d");

    var gradientStroke = this.ctx.createLinearGradient(0, 230, 0, 50);

    gradientStroke.addColorStop(1, 'rgba(233,32,16,0.2)');
    gradientStroke.addColorStop(0.4, 'rgba(233,32,16,0.0)');
    gradientStroke.addColorStop(0, 'rgba(233,32,16,0)'); //red colors

    var config = {
      type: 'line',
      data: {
        labels: this.chart_labels,
        datasets: [{
          label: "Tweets",
          fill: true,
          backgroundColor: gradientStroke,
          borderColor: '#ec250d',
          borderWidth: 2,
          borderDash: [],
          borderDashOffset: 0.0,
          pointBackgroundColor: '#ec250d',
          pointBorderColor: 'rgba(255,255,255,0)',
          pointHoverBackgroundColor: '#ec250d',
          pointBorderWidth: 20,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 15,
          pointRadius: 4,
          data: this.data,
        }]
      },
      options: gradientChartOptionsConfigurationWithTooltipRed
    };

    this.myChartData = new Chart(this.ctx, config);
    /*------------------------- END BIG CHART -------------------------*/
  }

  public updateOptions() {
    this.myChartData.data.datasets[0].data = this.data;
    this.myChartData.data.labels = this.chart_labels;
    this.myChartData.update();
  }

  private getTimeSum(dataset: any[], e: string) {
    let ret = {};

    for (let value of dataset) {
      if (value.sentiment === e) {
        var v = value.created_at.split("T")[1].slice(0, 5);
        ret[v] = (ret[v] || 0) + 1;
      }
    }

    return ret;
  }

}
