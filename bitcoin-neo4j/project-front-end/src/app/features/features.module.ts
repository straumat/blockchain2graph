import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {StatisticComponent} from './statistic/statistic.component';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';
import {ErrorComponent} from './error/error.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import { RemainingTimeComponent } from './remaining-time/remaining-time.component';


@NgModule({
    declarations: [StatisticComponent, CurrentBlockStatusComponent, ErrorComponent, RemainingTimeComponent],
    exports: [
        StatisticComponent,
        CurrentBlockStatusComponent,
        ErrorComponent,
        RemainingTimeComponent
    ],
    imports: [
        CommonModule,
        FontAwesomeModule
    ]
})
export class FeaturesModule {
}
