import {Component, OnInit, Input, HostBinding} from '@angular/core';

@Component({
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css']
})
export class StatisticComponent implements OnInit {

  @HostBinding('class') class = 'card mb-4 text-white';

  @Input() backgroundColor = 'bg-info';
  @Input() icon = 'fa-question';
  @Input() title = 'Component title';
  value  = 'n/a';

  constructor() {
  }

  ngOnInit() {
  }

}
