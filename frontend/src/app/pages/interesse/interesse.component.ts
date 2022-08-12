import { Component, OnInit } from '@angular/core';
import { ChartType, ChartOptions } from 'chart.js';
import { SingleDataSet, Label, monkeyPatchChartJsLegend, monkeyPatchChartJsTooltip } from 'ng2-charts';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-interesse',
  templateUrl: './interesse.component.html'
})
export class InteresseComponent  {
  // Pie
  public pieChartOptions: ChartOptions = {
    responsive: true,
  };
  public pieChartLabels: Label[] = [];
  public pieChartData: SingleDataSet = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartPlugins = [];
  public pieChartColors = [
    {
      backgroundColor: ['red', 'green', 'blue', '#999999', '#F44E3B', '#FE9200', '#FCDC00', '#DBDF00', '#A4DD00', '#68CCCA', '#73D8FF', '#AEA1FF', '#FDA1FF', '#333333', '#808080', '#cccccc', '#D33115', '#E27300', '#FCC400', '#B0BC00', '#68BC00', '#16A5A5', '#009CE0', '#7B64FF', '#FA28FF', '#000000', '#666666']
    },
  ];

  constructor(private http: HttpClient) {
    monkeyPatchChartJsTooltip();
    monkeyPatchChartJsLegend();
  }

  ngOnInit() {
    this.http.get<any>("http://localhost:8080/interesse/device").subscribe(res => {
//console.log("prova")
      //console.log(res);
      for (var key in res) {
        this.pieChartLabels.push(key);
        this.pieChartData.push(Number(res[key]))
        //console.log(key);
        //console.log(res[key]);
    }
    //console.log("superata")
    });
  }
}