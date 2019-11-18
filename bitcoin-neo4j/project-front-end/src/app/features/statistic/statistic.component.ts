import {Component, Input, OnInit} from '@angular/core';
import {Blockchain2graphService} from '../../core/services/blockchain2graph-service.service';

@Component({
    selector: 'app-statistic',
    templateUrl: './statistic.component.html',
    styleUrls: ['./statistic.component.css']
})
export class StatisticComponent implements OnInit {

    // Static values.
    static readonly nonAvailableValue = -1;
    static readonly nonAvailableDisplay = 'n/a';

    // Information type.
    static readonly typeBlockCountInBlockchain = 'blockCountInBlockchain';
    static readonly typeBlockCountInNeo4j = 'blockCountInNeo4j';
    static readonly lastBlockProcessDuration = 'lastBlockProcessDuration';

    // Components values.
    @Input() backgroundColor = 'bg-info';
    @Input() icon = 'fa-question';
    @Input() title = 'Component title';
    @Input() type: string;
    receivedValue = StatisticComponent.nonAvailableValue;
    displayedValue = StatisticComponent.nonAvailableDisplay;

    /**
     * Constructor.
     * @param blockchain2graphService blockchain2graph service.
     */
    constructor(private blockchain2graphService: Blockchain2graphService) {
    }

    /**
     * We subscribe to observable depending on component configuration.
     */
    ngOnInit() {
        // noinspection DuplicatedCode
        switch (this.type) {
            // Number of blocks in bitcoin core.
            case StatisticComponent.typeBlockCountInBlockchain:
                this.blockchain2graphService.blockCountInBlockchain.subscribe((value: number) => {
                    if (value != null) {
                        this.receivedValue = value;
                        if (value !== StatisticComponent.nonAvailableValue) {
                            this.updateDisplayedValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
                        } else {
                            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
                        }
                    }
                });
                break;
            // Number of blocks in neo4j.
            case StatisticComponent.typeBlockCountInNeo4j:
                this.blockchain2graphService.blockCountInNeo4j.subscribe((value: number) => {
                    if (value != null) {
                        this.receivedValue = value;
                        if (value !== StatisticComponent.nonAvailableValue) {
                            this.updateDisplayedValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
                        } else {
                            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
                        }
                    }
                });
                break;
            // Last block import duration.
            case StatisticComponent.lastBlockProcessDuration:
                this.blockchain2graphService.lastBlockProcessDuration.subscribe((value: number) => {
                    if (value != null) {
                        this.receivedValue = value;
                        if (value !== StatisticComponent.nonAvailableValue) {
                            const formattedValue = Intl.NumberFormat('en-us', {
                                minimumFractionDigits: 2,
                                maximumFractionDigits: 2
                            }).format(value / 1000) + ' s';
                            this.updateDisplayedValue(formattedValue);
                        } else {
                            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
                        }
                    }
                });
                break;
        }
    }

    /**
     * Update the value displayed by the block.
     * @param newDisplayedValue new value
     */
    updateDisplayedValue(newDisplayedValue: string) {
        this.displayedValue = newDisplayedValue;
    }

}
