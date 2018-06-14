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
  private readonly blockCountInBitcoinCoreSubject: BehaviorSubject<Object>;
  private readonly blockCountInNeo4jSubject: BehaviorSubject<Object>;
  private readonly lastBlockProcessDurationSubject: BehaviorSubject<Object>;
  private readonly currentBlockStatusSubject: BehaviorSubject<CurrentBlockStatus>;
  private readonly lastErrorMessageSubject: BehaviorSubject<Object>;

  // Observable of status values.
  public readonly blockCountInBitcoinCore: Observable<Object>;
  public readonly blockCountInNeo4j: Observable<Object>;
  public readonly lastBlockProcessDuration: Observable<Object>;
  public readonly currentBlockStatus: Observable<CurrentBlockStatus>;
  public readonly lastErrorMessage: Observable<Object>;

  /**
   * Constructor.
   */
  constructor() {
    // Instantiate a current block state saying nothing has been done yet.
    const nonAvailableBlockStatusValue = <CurrentBlockStatus>{};
    nonAvailableBlockStatusValue.blockHeight = Blockchain2graphService.nonAvailableValueNumber;
    nonAvailableBlockStatusValue.processStep = CurrentBlockStatusProcessStep.NO_BLOCK_TO_PROCESS;
    nonAvailableBlockStatusValue.transactionCount = Blockchain2graphService.nonAvailableValueNumber;
    nonAvailableBlockStatusValue.addressCount = Blockchain2graphService.nonAvailableValueNumber;
    nonAvailableBlockStatusValue.loadedTransactions = Blockchain2graphService.nonAvailableValueNumber;
    nonAvailableBlockStatusValue.processedAddresses = Blockchain2graphService.nonAvailableValueNumber;
    nonAvailableBlockStatusValue.processedTransactions = Blockchain2graphService.nonAvailableValueNumber;

    // Initiate subjects.
    this.blockCountInBitcoinCoreSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.blockCountInNeo4jSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.lastBlockProcessDurationSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueNumber);
    this.currentBlockStatusSubject = new BehaviorSubject<CurrentBlockStatus>(nonAvailableBlockStatusValue);
    this.lastErrorMessageSubject = new BehaviorSubject<Object>(Blockchain2graphService.nonAvailableValueString);

    // Initiate observables.
    this.blockCountInBitcoinCore = this.blockCountInBitcoinCoreSubject.asObservable();
    this.blockCountInNeo4j = this.blockCountInNeo4jSubject.asObservable();
    this.lastBlockProcessDuration = this.lastBlockProcessDurationSubject.asObservable();
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
    // blockCountInBitcoinCoreSubject.
    if (message.blockCountInBitcoinCore !== this.blockCountInBitcoinCoreSubject.getValue()) {
      this.blockCountInBitcoinCoreSubject.next(message.blockCountInBitcoinCore);
    }

    // blocksCountInNeo4j.
    if (message.blockCountInNeo4j !== this.blockCountInNeo4jSubject.getValue()) {
      this.blockCountInNeo4jSubject.next(message.blockCountInNeo4j);
    }

    // averageBlockProcessDuration.
    if (message.lastBlockProcessDuration !== this.lastBlockProcessDurationSubject.getValue()) {
      this.lastBlockProcessDurationSubject.next(message.lastBlockProcessDuration);
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
   * Returns true if the block status set as parameter is different from the one we previously had.
   * @param {CurrentBlockStatus} c current block status
   */
  private isCurrentBlockStatusDifferent(c: CurrentBlockStatus) {
    return  this.currentBlockStatusSubject.getValue() == null ||
            this.currentBlockStatusSubject.getValue().blockHeight !== c.blockHeight ||
            this.currentBlockStatusSubject.getValue().processStep !== c.processStep ||
            this.currentBlockStatusSubject.getValue().transactionCount !== c.transactionCount ||
            this.currentBlockStatusSubject.getValue().addressCount !== c.addressCount ||
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
