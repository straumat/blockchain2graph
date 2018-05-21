import {Component, HostBinding, OnInit} from '@angular/core';
import {Blockchain2graphService} from '../blockchain2graph.service';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  // Static values.
  static readonly nonAvailableValuerString = 'n/a';

  @HostBinding('class') class = 'card mb-4 text-white';

  constructor(private blockchain2graphService: Blockchain2graphService) { }

  viewError = false;
  lastErrorMessage = ErrorComponent.nonAvailableValuerString;

  ngOnInit() {
    this.blockchain2graphService.lastErrorMessage.subscribe((value: string) => {
      if (value !== ErrorComponent.nonAvailableValuerString) {
        this.lastErrorMessage = value;
        this.viewError = true;
      } else {
        this.viewError = false;
      }
    });
  }

}
