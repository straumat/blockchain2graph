import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Blockchain2graphService} from './blockchain2graph.service';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';
import {ErrorComponent} from './error/error.component';
import {Location, LocationStrategy, PathLocationStrategy} from '@angular/common';

@NgModule({
  imports: [
    BrowserModule
  ],
  declarations: [
    AppComponent,
    StatisticComponent,
    CurrentBlockStatusComponent,
    ErrorComponent
  ],
  providers: [
    Location, {provide: LocationStrategy, useClass: PathLocationStrategy},
    Blockchain2graphService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
