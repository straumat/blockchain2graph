import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {StatisticComponent} from './statistic/statistic.component';
import {CurrentBlockStatusComponent} from './current-block-status/current-block-status.component';
import {ErrorComponent} from './error/error.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';


@NgModule({
    declarations: [StatisticComponent, CurrentBlockStatusComponent, ErrorComponent],
    exports: [
        StatisticComponent,
        CurrentBlockStatusComponent,
        ErrorComponent
    ],
    imports: [
        CommonModule,
        FontAwesomeModule
    ]
})
export class FeaturesModule {
}
