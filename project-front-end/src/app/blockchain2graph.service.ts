import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {Blockchain2graphMessageType} from './Blockchain2graphMessageType';

@Injectable()
export class Blockchain2graphService implements OnDestroy {

  // Websocket connexion.
  private static serverUrl = 'ws://localhost:8080/status/websocket';
  private webSocket: WebSocket;

  // Status values.
  private readonly blocksInBitcoinCoreSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>('n/a');
  private readonly blocksInNeo4jSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>('n/a');
  private readonly blockImportDurationSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>('n/a');

  // Observable.
  public readonly blocksInBitcoinCore: Observable<Object> = this.blocksInBitcoinCoreSubject.asObservable();
  public readonly blocksInNeo4j: Observable<Object> = this.blocksInNeo4jSubject.asObservable();
  public readonly blockImportDuration: Observable<Object> = this.blockImportDurationSubject.asObservable();

  constructor() {
    this.webSocket = new WebSocket(Blockchain2graphService.serverUrl);
    this.webSocket.addEventListener('message', message => {
      this.processMessage(JSON.parse(message.data));
    });
  }

  public processMessage(b2gMessage) {
    switch (b2gMessage.messageType) {
      // Number of blocks in bitcoin core.
      case Blockchain2graphMessageType.BLOCKS_IN_BITCOIN_CORE:
        this.blocksInBitcoinCoreSubject.next(b2gMessage.messageValue);
        break;
      // Number of blocks in neo4j.
      case Blockchain2graphMessageType.BLOCKS_IN_NEO4J:
        this.blocksInNeo4jSubject.next(b2gMessage.messageValue);
        break;
      // Average block import duration.
      case Blockchain2graphMessageType.BLOCK_IMPORT_DURATION:
        this.blockImportDurationSubject.next(b2gMessage.messageValue);
        break;
    }
  }

  ngOnDestroy(): void {
    this.webSocket.close();
  }

}
