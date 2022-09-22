describe('Smoke test', () => {
  it('Visits the initial project page', () => {
    cy.visit('/')
    cy.contains('Rock, Paper, Scissors')
    cy.contains('Reset').click();
    cy.contains('0 | 0');
    cy.get('#idWeaponPAPER').click();

    cy.get('#selectedByComputer').then(($el) => {
      let html = $el.html()
      if(html.includes('paper')){
        cy.get('#result').contains('It is a tie')
      } else if(html.includes('rock')){
        cy.get('#result').contains('You win')
      } else {
        cy.get('#result').contains('You lose')
      }
      console.log("Test completed")
    });
  })
})


