import {Component, OnInit, Input, HostBinding} from '@angular/core';
import {Blockchain2graphService} from '../blockchain2graph.service';
import {Blockchain2graphMessageType} from '../Blockchain2graphMessageType';

@Component({
  selector: 'app-statistic',
  templateUrl: './statistic.component.html',
  styleUrls: ['./statistic.component.css'],
  providers: [Blockchain2graphService]
})
export class StatisticComponent implements OnInit {

  @HostBinding('class') class = 'card mb-4 text-white';

  @Input() backgroundColor = 'bg-info';
  @Input() icon = 'fa-question';
  @Input() title = 'Component title';
  @Input() type: string;
  value = 'n/a';

  constructor(private blockchain2graphService: Blockchain2graphService) {
  }

  ngOnInit() {
    switch (this.type) {
      case Blockchain2graphMessageType.BLOCKS_IN_BITCOIN_CORE:
        this.blockchain2graphService.blocksInBitcoinCore.subscribe((value: string) => {
          if (value !== 'n/a') {
            this.updateValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
          }
        });
        break;
      // Number of blocks in neo4j.
      case Blockchain2graphMessageType.BLOCKS_IN_NEO4J:
        this.blockchain2graphService.blocksInNeo4j.subscribe((value: string) => {
          if (value !== 'n/a') {
            this.updateValue(value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' '));
          }
        });
        break;
      // Average block import duration.
      case Blockchain2graphMessageType.BLOCK_IMPORT_DURATION:
        this.blockchain2graphService.blockImportDuration.subscribe((value: string) => {
          if (value !== 'n/a') {
            this.updateValue(parseFloat(value).toFixed(2) + ' s');
          }
        });
        break;
    }
  }

  updateValue(newValue: string) {
    this.value = newValue;
  }

}
