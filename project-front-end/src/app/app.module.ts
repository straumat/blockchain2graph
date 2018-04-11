import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {StatisticComponent} from './statistic/statistic.component';
import {Blockchain2graphService} from './blockchain2graph.service';


@NgModule({
  declarations: [
    AppComponent,
    StatisticComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [Blockchain2graphService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
