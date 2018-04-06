import {Component, OnInit, Input} from '@angular/core';

@Component({
  host: {
    'class': 'card mb-4 text-white'
  },
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css']
})
export class StatisticComponent implements OnInit {

  @Input() backgroundColor: string;
  @Input() icon: string;
  @Input() title: string;
  value: string;
  defaultValue: string;

  constructor() {
    this.backgroundColor = 'bg-info';
    this.icon = 'fa-question';
    this.title = 'Component title';
    this.defaultValue = 'n/a';
    this.value = this.defaultValue;
  }

  ngOnInit() {
  }

}
