import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Blockchain2graphService} from './blockchain2graph.service';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';


@NgModule({
  declarations: [
    AppComponent,
    StatisticComponent,
    CurrentBlockStatusComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [Blockchain2graphService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
