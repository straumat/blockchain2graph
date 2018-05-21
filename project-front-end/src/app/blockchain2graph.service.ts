import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {CurrentBlockStatus, CurrentBlockStatusProcessStep} from './project-back-end';

@Injectable()
export class Blockchain2graphService implements OnDestroy {

  // Static values.
  static readonly nonAvailableValueNumber = -1;
  static readonly nonAvailableValueString = 'n/a';

  // Websocket connexion.
  private static serverUrl = 'ws://localhost:8080/status/websocket';
  private webSocket: WebSocket;

  // Status values.
  private readonly blocksCountInBitcoinCoreSubject: BehaviorSubject<Object>;
  private readonly blocksCountInNeo4jSubject: BehaviorSubject<Object>;
  private readonly averageBlockProcessDurationSubject: BehaviorSubject<Object>;
  private readonly currentBlockStatusSubject: BehaviorSubject<CurrentBlockStatus>;
  private readonly lastErrorMessageSubject: BehaviorSubject<Object>;

  // Observable.
  public readonly blocksCountInBitcoinCore: Observable<Object>;
  public readonly blocksCountInNeo4j: Observable<Object>;
  public readonly averageBlockProcessDuration: Observable<Object>;
  public readonly currentBlockStatus: Observable<CurrentBlockStatus>;
  public readonly lastErrorMessage: Observable<Object>;

  /**
   * Constructor.
   */
  constructor() {
    // Instantiate a current block state.
    const currentBlockStatus = <CurrentBlockStatus>{};
    currentBlockStatus.blockHeight = Blockchain2graphService.nonAvailableValueNumber;
    currentBlockStatus.processStep = CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;
    currentBlockStatus.transactionsCount = Blockchain2graphService.nonAvailableValueNumber;
    currentBlockStatus.addressesCount = Blockchain2graphService.nonAvailableValueNumber;
    currentBlockStatus.loadedTransactions = Blockchain2graphService.nonAvailableValueNumber;
    currentBlockStatus.processedAddresses = Blockchain2graphService.nonAvailableValueNumber;
    currentBlockStatus.processedTransactions = Blockchain2graphService.nonAvailableValueNumber;

    // Initiate observer.
    this.blocksCountInBitcoinCoreSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.blocksCountInNeo4jSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.averageBlockProcessDurationSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.currentBlockStatusSubject = new BehaviorSubject<CurrentBlockStatus>(currentBlockStatus);
    this.lastErrorMessageSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueString);

    // Observable.
    this.blocksCountInBitcoinCore = this.blocksCountInBitcoinCoreSubject.asObservable();
    this.blocksCountInNeo4j = this.blocksCountInNeo4jSubject.asObservable();
    this.averageBlockProcessDuration = this.averageBlockProcessDurationSubject.asObservable();
    this.currentBlockStatus = this.currentBlockStatusSubject.asObservable();
    this.lastErrorMessage = this.lastErrorMessageSubject.asObservable();

    // Connecting and subscribing to the websocket.
    this.webSocket = new WebSocket(Blockchain2graphService.serverUrl);
    this.webSocket.addEventListener('message', message => {
      this.processMessage(JSON.parse(message.data));
    });
  }

  /**
   * Triggered when a new message is coming from the server.
   * @param message blockchain2graph server message.
   */
  public processMessage(message) {
    // blocksCountInBitcoinCoreSubject.
    if (message.blocksCountInBitcoinCore !== this.blocksCountInBitcoinCoreSubject.getValue()) {
      this.blocksCountInBitcoinCoreSubject.next(message.blocksCountInBitcoinCore);
    }

    // blocksCountInNeo4j.
    if (message.blocksCountInNeo4j !== this.blocksCountInNeo4jSubject.getValue()) {
      this.blocksCountInNeo4jSubject.next(message.blocksCountInNeo4j);
    }

    // averageBlockProcessDuration.
    if (message.averageBlockProcessDuration !== this.averageBlockProcessDurationSubject.getValue()) {
      this.averageBlockProcessDurationSubject.next(message.averageBlockProcessDuration);
    }

    // currentBlockStatus.
    if (this.isCurrentBlockStatusDifferent(message.currentBlockStatus)) {
      this.currentBlockStatusSubject.next(message.currentBlockStatus);
    }

    // lastErrorMessage.
    if (message.lastErrorMessage !== this.lastErrorMessageSubject.getValue()) {
      this.lastErrorMessageSubject.next((message.lastErrorMessage));
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

  /**
   * On close, we disconnect from the server.
   */
  ngOnDestroy(): void {
    this.webSocket.close();
  }

}
