import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {CoreRoutingModule} from './core-routing.module';
import {CoreComponent} from './core.component';
import {HeaderComponent} from './components/header/header.component';
import {FeaturesModule} from '../features/features.module';


@NgModule({
    declarations: [CoreComponent, HeaderComponent],
    imports: [
        CommonModule,
        CoreRoutingModule,
        FeaturesModule,
    ]
})
export class CoreModule {
}
