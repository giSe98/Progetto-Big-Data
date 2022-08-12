import { AfterViewInit, Component, TemplateRef, ViewChild, OnInit } from "@angular/core";
import { IgxSizeScaleComponent } from "igniteui-angular-charts";
import { IgxValueBrushScaleComponent } from "igniteui-angular-charts";
import { MarkerType } from "igniteui-angular-charts";
import { IgxShapeDataSource } from "igniteui-angular-core";
import { IgxGeographicMapComponent } from "igniteui-angular-maps";
import { IgxGeographicProportionalSymbolSeriesComponent } from "igniteui-angular-maps";

import { WorldLocations } from "./WorldLocations";
import { WorldLocationsRealTime } from './WorldLocationsRealTime';

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

    @ViewChild("mapRealTime", { static: true })
    public mapRealTime: IgxGeographicMapComponent;
    @ViewChild("templateRealTime", { static: true })
    public tooltipTemplateRealTime: TemplateRef<object>;

    public countries: any[] = [];
    public values: any[] = [];

    public countriesRealTime: any[] = [];
    public valuesRealTime: any[] = [];

    constructor() {}

    public ngAfterViewInit(): void {
        const sds = new IgxShapeDataSource();
        sds.importCompleted.subscribe(() => this.onDataLoaded(sds, "normal"));
        sds.shapefileSource = "https://static.infragistics.com/xplatform/shapes/WorldCities.shp";
        sds.databaseSource  = "https://static.infragistics.com/xplatform/shapes/WorldCities.dbf";
        sds.dataBind();

        const sds2 = new IgxShapeDataSource();
        sds2.importCompleted.subscribe(() => this.onDataLoaded(sds2, "real time"));
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
            if (temp % 10 === 0 && temp >= 0) {
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
        if (e === "normal") {
          this.addSeriesWith(WorldLocations.getAll(), e);
          
        } else if (e === "real time") {
          this.addSeriesWith(WorldLocationsRealTime.getAll(), e);
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


        if (e === "normal") {
          symbolSeries.tooltipTemplate = this.tooltipTemplate;
          this.map.series.add(symbolSeries);
          this.countries = WorldLocations.getCountries();
          this.values = WorldLocations.getCountrySum();
        } else if (e === "real time") {
          symbolSeries.tooltipTemplate = this.tooltipTemplateRealTime;
          this.mapRealTime.series.add(symbolSeries);
          this.countriesRealTime = WorldLocationsRealTime.getCountries();
          this.valuesRealTime = WorldLocationsRealTime.getCountrySum();
        }
    }
}
