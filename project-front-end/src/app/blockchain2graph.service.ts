import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {CurrentBlockStatus} from './project-back-end';

@Injectable()
export class Blockchain2graphService implements OnDestroy {

  // Static values.
  static readonly nonAvailableValue = -1;

  // Websocket connexion.
  private static serverUrl = 'ws://localhost:8080/status/websocket';
  private webSocket: WebSocket;

  // Status values.
  private readonly blocksCountInBitcoinCoreSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);
  private readonly blocksCountInNeo4jSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);
  private readonly averageBlockProcessDurationSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);
  private readonly currentBlockStatusSubject: BehaviorSubject<CurrentBlockStatus> = new BehaviorSubject<CurrentBlockStatus>(null);

  // Observable.
  public readonly blocksCountInBitcoinCore: Observable<Object> = this.blocksCountInBitcoinCoreSubject.asObservable();
  public readonly blocksCountInNeo4j: Observable<Object> = this.blocksCountInNeo4jSubject.asObservable();
  public readonly averageBlockProcessDuration: Observable<Object> = this.averageBlockProcessDurationSubject.asObservable();
  public readonly currentBlockStatus: Observable<CurrentBlockStatus> = this.currentBlockStatusSubject.asObservable();

  /**
   * Constructor.
   */
  constructor() {
    // Connecting and subscribing to the websocket.
    this.webSocket = new WebSocket(Blockchain2graphService.serverUrl);
    this.webSocket.addEventListener('message', message => {
      this.processMessage(JSON.parse(message.data));
    });

    // Instantiate a current block state.
    const currentBlockStatus = <CurrentBlockStatus>{};
    currentBlockStatus.blockHeight = Blockchain2graphService.nonAvailableValue;
    currentBlockStatus.processStep = 'NOTHING_TO_PROCESS';
    currentBlockStatus.transactionsCount = Blockchain2graphService.nonAvailableValue;
    currentBlockStatus.addressesCount = Blockchain2graphService.nonAvailableValue;
    currentBlockStatus.loadedTransactions = Blockchain2graphService.nonAvailableValue;
    currentBlockStatus.processedAddresses = Blockchain2graphService.nonAvailableValue;
    currentBlockStatus.processedTransactions = Blockchain2graphService.nonAvailableValue;
    this.currentBlockStatusSubject.next(currentBlockStatus);
  }

  /**
   * Triggered when a new message is coming from the server.
   * @param message blockchain2graph server message.
   */
  public processMessage(message) {
    const status = JSON.parse(message);

    // blocksCountInBitcoinCoreSubject.
    if (status.blocksCountInBitcoinCore !== this.blocksCountInBitcoinCoreSubject.getValue()) {
      this.blocksCountInBitcoinCoreSubject.next(status.blocksCountInBitcoinCore);
    }

    // blocksCountInNeo4j.
    if (status.blocksCountInNeo4j !== this.blocksCountInNeo4jSubject.getValue()) {
      this.blocksCountInNeo4jSubject.next(status.blocksCountInNeo4j);
    }

    // averageBlockProcessDuration.
    if (status.averageBlockProcessDuration !== this.averageBlockProcessDurationSubject.getValue()) {
      this.averageBlockProcessDurationSubject.next(status.averageBlockProcessDuration);
    }

    // currentBlockStatus.
    if (this.isCurrentBlockStatusDifferent(status.currentBlockStatus)) {
      this.currentBlockStatusSubject.next(status.currentBlockStatus);
    }
  }

  /**
   * Returns true if the block as parameter is different from the one we previously had.
   * @param {CurrentBlockStatus} c current block status
   */
  private isCurrentBlockStatusDifferent(c: CurrentBlockStatus) {
    return  this.currentBlockStatusSubject.getValue() == null ||
            this.currentBlockStatusSubject.getValue().blockHeight !== c.blockHeight ||
            this.currentBlockStatusSubject.getValue().processStep !== c.processStep ||
            this.currentBlockStatusSubject.getValue().transactionsCount !== c.transactionsCount ||
            this.currentBlockStatusSubject.getValue().addressesCount !== c.addressesCount ||
            this.currentBlockStatusSubject.getValue().loadedTransactions !== c.loadedTransactions ||
            this.currentBlockStatusSubject.getValue().processedAddresses !== c.processedAddresses ||
            this.currentBlockStatusSubject.getValue().processedTransactions !== c.processedTransactions;
  }

  ngOnDestroy(): void {
    this.webSocket.close();
  }

}
