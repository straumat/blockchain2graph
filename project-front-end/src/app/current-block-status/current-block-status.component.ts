import {Component, HostBinding, OnInit} from '@angular/core';

@Component({
  selector: 'app-current-block-status',
  templateUrl: './current-block-status.component.html',
  styleUrls: ['./current-block-status.component.css']
})
export class CurrentBlockStatusComponent implements OnInit {

  static readonly nonAvailableValue = -1;
  static readonly nonAvailableDisplay = 'n/a';

  @HostBinding('class') class = 'card mb-4 text-white';

  blockHeight = CurrentBlockStatusComponent.nonAvailableDisplay;
  processStep = CurrentBlockStatusComponent.nonAvailableDisplay;
  progression = 15;

  constructor() { }

  ngOnInit() {
  }

}
