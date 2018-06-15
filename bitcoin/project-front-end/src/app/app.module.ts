import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {Blockchain2graphService} from './blockchain2graph.service';
import {StatisticComponent} from './statistic/statistic.component';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';
import {ErrorComponent} from './error/error.component';

@NgModule({
  imports: [
    BrowserModule
  ],
  declarations: [
    AppComponent,
    CurrentBlockStatusComponent,
    StatisticComponent,
    ErrorComponent
  ],
  providers: [
    Blockchain2graphService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
