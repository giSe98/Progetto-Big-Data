import { DashboardComponent } from './../dashboard/dashboard.component';
import { AfterViewInit, Component, TemplateRef, ViewChild, OnInit } from "@angular/core";
import { IgxSizeScaleComponent } from "igniteui-angular-charts";
import { IgxValueBrushScaleComponent } from "igniteui-angular-charts";
import { MarkerType } from "igniteui-angular-charts";
import { IgxShapeDataSource } from "igniteui-angular-core";
import { IgxGeographicMapComponent } from "igniteui-angular-maps";
import { IgxGeographicProportionalSymbolSeriesComponent } from "igniteui-angular-maps";
import { HttpClient } from '@angular/common/http';

@Component({
  selector: "app-maps",
  templateUrl: "./maps.component.html"
})
export class MapsComponent implements AfterViewInit {
    name = 'Angular Basic Examples';

    @ViewChild("map", { static: true })
    public map: IgxGeographicMapComponent;
    @ViewChild("template", { static: true })
    public tooltipTemplate: TemplateRef<object>;

    @ViewChild("mapRelativePositions", { static: true })
    public mapRelativePositions: IgxGeographicMapComponent;
    @ViewChild("templateRelativePositions", { static: true })
    public tooltipTemplateRelativePositions: TemplateRef<object>;

    public relativePositions: any[] = [];
    public countriesRelativePositions: any[] = [];
    public valuesRelativePositions: any[] = [];

    public realPositions: any[] = [];
    public countriesRealPositions: any[] = [];
    public valuesRealPositions: any[] = [];

    public percentage: number;
    public total: number;

    constructor(private http: HttpClient) {}

    public ngAfterViewInit(): void {
      this.http.get<any>("http://localhost:8080/maps/relativePositions").subscribe(res => {
        this.relativePositions = res;
        for (let i = 0; i < this.relativePositions.length; i++) {
          var a = atob(this.relativePositions[i].name);
          this.relativePositions[i].name = a;
        }
      });

      this.http.get<any>("http://localhost:8080/maps/realPositions").subscribe(res => {
        this.realPositions = res;
        for (let i = 0; i < this.realPositions.length; i++) {
          var a = atob(this.realPositions[i].name);
          this.realPositions[i].name = a;
        }
      });

      this.http.get<any>("http://localhost:8080/dashboard/tweets").subscribe(res => {
        this.total = res.length;
      });


        const sds = new IgxShapeDataSource();
        sds.importCompleted.subscribe(() => this.onDataLoaded(sds, "real"));
        sds.shapefileSource = "https://static.infragistics.com/xplatform/shapes/WorldTemperatures.shp";
        sds.databaseSource  = "https://static.infragistics.com/xplatform/shapes/WorldTemperatures.dbf";
        sds.dataBind();

        const sds2 = new IgxShapeDataSource();
        sds2.importCompleted.subscribe(() => this.onDataLoaded(sds2, "relative"));
        sds2.shapefileSource = "https://static.infragistics.com/xplatform/shapes/WorldCities.shp";
        sds2.databaseSource  = "https://static.infragistics.com/xplatform/shapes/WorldCities.dbf";
        sds2.dataBind();
    }

    public onDataLoaded(sds: IgxShapeDataSource, e: any) {
        const shapeRecords = sds.getPointData();
        // console.log("loaded contour shapes: " + shapeRecords.length + " from /Shapes/WorldTemperatures.shp");

        const contourPoints: any[] = [];
        for (const record of shapeRecords) {
            const temp = record.fieldValues.Contour;
            // using only major contours (every 10th degrees Celsius)
            if (temp % 15 === 0 && temp >= 0) {
                for (const shapes of record.points) {
                    for (let i = 0; i < shapes.length; i++) {
                        if (i % 5 === 0) {
                            const p = shapes[i];
                            const item = { lon: p.x, lat: p.y, value: temp };
                            contourPoints.push(item);
                        }
                    }
                }
            }
        }

        // console.log("loaded contour points: " + contourPoints.length);
        if (e === "real") { 
          this.addSeriesWith(this.realPositions, e);
        } else if (e === "relative") {
          this.addSeriesWith(this.relativePositions, e);
        }
    }


    public addSeriesWith(locations: any[], e: any) {
        const sizeScale = new IgxSizeScaleComponent();
        sizeScale.minimumValue = 4;
        sizeScale.maximumValue = 60;

        const brushes = [
            "rgba(14, 194, 14, 0.4)",  // semi-transparent green
            "rgba(252, 170, 32, 0.4)", // semi-transparent orange
            "rgba(252, 32, 32, 0.4)"  // semi-transparent red
        ];

        const brushScale = new IgxValueBrushScaleComponent();
        brushScale.brushes = brushes;
        brushScale.minimumValue = 0;
        brushScale.maximumValue = 30;

        const symbolSeries = new IgxGeographicProportionalSymbolSeriesComponent();
        symbolSeries.dataSource = locations;
        symbolSeries.markerType = MarkerType.Circle;
        symbolSeries.radiusScale = sizeScale;
        symbolSeries.fillScale = brushScale;
        symbolSeries.fillMemberPath = "pop";
        symbolSeries.radiusMemberPath = "pop";
        symbolSeries.latitudeMemberPath = "lat";
        symbolSeries.longitudeMemberPath = "lon";
        symbolSeries.markerOutline = "rgba(0,0,0,0.3)";


        if (e === "real") {
          symbolSeries.tooltipTemplate = this.tooltipTemplate;
          this.map.series.add(symbolSeries);
          this.countriesRealPositions = this.getCountries(this.realPositions);
          this.valuesRealPositions = this.getCountrySum(this.realPositions);
        } else if (e === "relative") {
          symbolSeries.tooltipTemplate = this.tooltipTemplateRelativePositions;
          this.mapRelativePositions.series.add(symbolSeries);
          this.countriesRelativePositions = this.getCountries(this.relativePositions);
          this.valuesRelativePositions = this.getCountrySum(this.relativePositions);
        }
    }

    private getCountries(loc: any[]): any[] {
      if(loc.length !== 0) {
        return loc.map((get) => get.country).filter((v, i, a) => a.indexOf(v) === i);
      }
      return [];
    }

    private getCountrySum(loc: any[]) {
      let ret = {};
  
      loc.forEach(el => {
        ret[el.country] = (ret[el.country] || 0) + 1;
      })
  
      var values = []
      for(var k in ret){
        values.push(ret[k]);
      }
  
      return values;
    }

}
