import {Component, OnInit} from '@angular/core';
import {Blockchain2graphService} from '../../core/services/blockchain2graph-service.service';
import {faExclamationTriangle} from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrls: ['./error.component.css']
})
export class ErrorComponent implements OnInit {

  // Static values.
  static readonly nonAvailableValuerString = 'n/a';

  faExclamationTriangle = faExclamationTriangle;

  /**
   * Constructor.
   * @param blockchain2graphService service
   */
  constructor(private blockchain2graphService: Blockchain2graphService) { }

  // component values.
  viewError = false;
  lastErrorMessage = ErrorComponent.nonAvailableValuerString;

  /**
   * Subscribe to error observable.
   */
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
