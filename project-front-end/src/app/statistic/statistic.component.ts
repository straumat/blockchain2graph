import {Component, OnInit, Input, HostBinding} from '@angular/core';
import {Blockchain2graphService} from '../blockchain2graph.service';

@Component({
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css']
})
export class StatisticComponent implements OnInit {

  static readonly nonAvailableValue = -1;
  static readonly nonAvailableDisplay = 'n/a';

  static readonly typeBlocksCountInBitcoinCore = 'blocksCountInBitcoinCore';
  static readonly typeBlocksCountInNeo4j = 'blocksCountInNeo4j';
  static readonly typeAverageBlockProcessDuration = 'averageBlockProcessDuration';

  @HostBinding('class') class = 'card mb-4 text-white';

  @Input() backgroundColor = 'bg-info';
  @Input() icon = 'fa-question';
  @Input() title = 'Component title';
  @Input() type: string;
  receivedValue = -1;
  displayedValue = 'n/a';

  constructor(private blockchain2graphService: Blockchain2graphService) {
  }

  ngOnInit() {
    switch (this.type) {
      // Number of blocks in bitcoin core.
      case StatisticComponent.typeBlocksCountInBitcoinCore:
        this.blockchain2graphService.blocksCountInBitcoinCore.subscribe((value: number) => {
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
        this.blockchain2graphService.blocksCountInNeo4j.subscribe((value: number) => {
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
        this.blockchain2graphService.averageBlockProcessDuration.subscribe((value: number) => {
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

  updateDisplayedValue(newDisplayedValue: string) {
    this.displayedValue = newDisplayedValue;
  }

}
