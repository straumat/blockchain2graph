import {Component, OnInit} from '@angular/core';
import {Blockchain2graphService} from '../../core/services/blockchain2graph-service.service';
import {ApplicationStatus} from '../../shared/blockchain2graph-bitcoin-neo4j-back-end';
import {faCalendarCheck} from '@fortawesome/free-solid-svg-icons';
import * as moment from 'moment';

@Component({
    selector: 'app-remaining-time',
    templateUrl: './remaining-time.component.html',
    styleUrls: ['./remaining-time.component.css']
})
export class RemainingTimeComponent implements OnInit {

    faCalendarCheck = faCalendarCheck;

    remainingTime = 'Remaining time not available for the moment';
    lastUpdate;

    /**
     * Constructor.
     */
    constructor(private blockchain2graphService: Blockchain2graphService) {
    }

    ngOnInit() {
        this.blockchain2graphService.applicationStatus.subscribe((value: ApplicationStatus) => {
            if (value != null && value.blockCountInBlockchain > 0) {
                // If there was no update and or 1 minute has passed.
                if ((this.lastUpdate == null) || (moment().subtract(1, 'minutes') > this.lastUpdate)) {
                    if (value.blockCountInBlockchain - value.blockCountInNeo4j > 2) {
                        // tslint:disable-next-line:max-line-length
                        const duration = moment.duration((value.blockCountInBlockchain - value.blockCountInNeo4j) * value.lastBlockProcessDuration);
                        this.remainingTime = Math.ceil(duration.asDays()) + ' day(s) remaining';
                    } else {
                        // We are done.
                        this.remainingTime = 'Synchronized !';
                    }
                }
                this.lastUpdate = moment();
            }
        });
    }

}
