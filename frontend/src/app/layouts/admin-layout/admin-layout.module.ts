import { NgModule } from "@angular/core";
import { HttpClientModule } from "@angular/common/http";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { MatSelectModule } from '@angular/material/select';
import { AngularMyDatePickerModule } from 'angular-mydatepicker';
import { ChartsModule } from 'ng2-charts';

import { IgxGeographicMapModule } from 'igniteui-angular-maps';
import { IgxDataChartInteractivityModule } from 'igniteui-angular-charts';

import { AdminLayoutRoutes } from "./admin-layout.routing";
import { DashboardComponent } from "../../pages/dashboard/dashboard.component";
import { InteresseComponent } from './../../pages/interesse/interesse.component';
import { MapsComponent } from "../../pages/maps/maps.component";

/*import { MapComponent } from "../../pages/map/map.component";
import { NotificationsComponent } from "../../pages/notifications/notifications.component";
import { UserComponent } from "../../pages/user/user.component";
import { TablesComponent } from "../../pages/tables/tables.component";
import { TypographyComponent } from "../../pages/typography/typography.component";
// import { RtlComponent } from "../../pages/rtl/rtl.component";*/

import { NgbModule } from "@ng-bootstrap/ng-bootstrap";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(AdminLayoutRoutes),
    FormsModule,
    HttpClientModule,
    NgbModule,
    MatSelectModule,
    AngularMyDatePickerModule,
    ChartsModule,
    IgxGeographicMapModule,
    IgxDataChartInteractivityModule
  ],
  declarations: [
    DashboardComponent, 
    InteresseComponent,
    MapsComponent/*,
    TablesComponent,
    IconsComponent,
    TypographyComponent,
    NotificationsComponent,
    MapComponent,
    // RtlComponent*/
  ]
})
export class AdminLayoutModule {}
