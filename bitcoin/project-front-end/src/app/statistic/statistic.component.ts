import {Component, OnInit, Input, HostBinding} from '@angular/core';
import {Blockchain2graphService} from '../blockchain2graph.service';

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
  static readonly typeBlocksCountInBitcoinCore = 'blockCountInBitcoinCore';
  static readonly typeBlocksCountInNeo4j = 'blockCountInNeo4j';
  static readonly typeAverageBlockProcessDuration = 'lastBlockProcessDuration';

  // TODO Ask Cyrille about it.
  @HostBinding('class') class = 'card mb-4 text-white';

  // Compnents values.
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
    switch (this.type) {
      // Number of blocks in bitcoin core.
      case StatisticComponent.typeBlocksCountInBitcoinCore:
        this.blockchain2graphService.blockCountInBitcoinCore.subscribe((value: number) => {
          this.receivedValue = value;
          if (value !== StatisticComponent.nonAvailableValue) {
            this.updateDisplayedValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
          } else {
            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
          }
        });
        break;
      // Number of blocks in neo4j.
      case StatisticComponent.typeBlocksCountInNeo4j:
        this.blockchain2graphService.blockCountInNeo4j.subscribe((value: number) => {
          this.receivedValue = value;
          if (value !== StatisticComponent.nonAvailableValue) {
            this.updateDisplayedValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
          } else {
            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
          }
        });
        break;
      // Average block import duration.
      case StatisticComponent.typeAverageBlockProcessDuration:
        this.blockchain2graphService.lastBlockProcessDuration.subscribe((value: number) => {
          this.receivedValue = value;
          if (value !== StatisticComponent.nonAvailableValue) {
            const formatedValue = Intl.NumberFormat('en-us', {minimumFractionDigits: 2, maximumFractionDigits: 2}).format(value) + ' s';
            this.updateDisplayedValue(formatedValue);
          } else {
            this.updateDisplayedValue(StatisticComponent.nonAvailableDisplay);
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
